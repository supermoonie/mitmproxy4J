new Vue({
    el: '#main',
    data: function () {
        return {
            currentFlowId: '',
            urlFilter: '',
            activeIndex: '1',
            contentsTabPosition: 'left',
            activeName: 'Overview',
            currentOverview: [],
            currentRequestHeaders: [],
            currentRequestQueryString: [],
            currentRequestCookies: [],
            currentResponseHeaders: [],
            currentResponseCookies: [],
            flows: [],
            contentTypeList: [
                "application/json",
                "multipart/form-data",
                "application/x-www-form-urlencoded",
                "application/octet-stream",
                "text/html", "text/plain", "text/xml", "image/gif", "image/jpeg", "image/png",
                "application/xhtml+xml", "application/xml", "application/atom+xml", "application/pdf", "application/msword"
            ],
            timer: "",
            search: {
                host: "",
                port: "",
                contentType: ""
            }
        }
    },
    methods: {
        fetch() {
            const that = this;
            axios.get('http://127.0.0.1:8866/flow/fetch?host=' + this.search.host + '&port' + this.search.port + '&contentType=' + this.search.contentType + '&start=2020-06-11 07:54:00')
                .then(function (response) {
                    that.flows = response.data;
                    console.log(that.flows);
                })
                .catch(function (error) {
                    console.log(error);
                });
        },
        handleSelect(key, keyPath) {
            console.log(key, keyPath);
        },
        handleUrlClicked(id, data, event) {
            this.currentFlowId = data.request.id;
            this.currentOverview = [];
            this.currentOverview.push({
                'name': 'Time',
                'value': this.dateFormat('YYYY-mm-dd HH:MM:SS', new Date(data.request.timeCreated))
            });
            this.currentOverview.push({'name': 'Uri', 'value': data.request.uri});
            this.currentOverview.push({'name': 'Method', 'value': data.request.method});
            this.currentOverview.push({'name': 'Host', 'value': data.request.host});
            this.currentOverview.push({'name': 'Port', 'value': data.request.port});
            this.currentOverview.push({'name': 'HttpVersion', 'value': data.request.httpVersion});
            this.currentOverview.push({'name': 'Response Code', 'value': !data.response ? '-' : data.response.status});
            this.currentOverview.push({'name': 'Content-Type', 'value': !data.response ? '-' : data.response.contentType});

            this.currentRequestHeaders = data.requestHeaders;
            this.currentRequestQueryString = this.getQueryString(data.request.uri);
            let requestCookies = data.requestHeaders.filter(header => header.name === 'Cookie');
            this.currentRequestCookies = [];
            if (requestCookies.length !== 0) {
                let cookie = requestCookies[0].value;
                let cookieFieldList = cookie.split(";");
                for (let i = 0; i < cookieFieldList.length; i++) {
                    this.currentRequestCookies.push({
                        'name': cookieFieldList[i].split("=")[0],
                        'value': cookieFieldList[i].split("=")[1].trim()
                    })
                }
            }
            this.currentResponseHeaders = [];
            this.currentResponseCookies = [];
            if (!!data.responseHeaders) {
                this.currentResponseHeaders = data.responseHeaders;
                let responseCookies = data.responseHeaders.filter(header => header.name === 'Set-Cookie');
                if (responseCookies.length !== 0) {
                    for (let i = 0; i < responseCookies.length; i ++) {
                        let cookieText = responseCookies[i].value;
                        let cookieFieldList = cookieText.split(";");
                        let cookie = {};
                        for (let i = 0; i < cookieFieldList.length; i++) {
                            let field = cookieFieldList[i].split('=')[0].toLowerCase().trim();
                            let value = cookieFieldList[i].split('=')[1];
                            if (field === 'httponly') {
                                cookie['HTTPOnly'] = true;
                            } else if (field === 'secure') {
                                cookie['secure'] = true;
                            } else if (field === 'max-age') {
                                cookie['Max-Age'] = value;
                            } else if (field === 'path') {
                                cookie['path'] = value;
                            } else {
                                cookie['name'] = cookieFieldList[i].split('=')[0];
                                cookie['value'] = value;
                            }
                        }
                        this.currentResponseCookies.push(cookie);
                    }
                }
            }
        },
        getQueryString(uri) {
            let queryList = [];
            if (uri.indexOf("?") !== -1) {
                let query = uri.split("?")[1];
                let arr = query.split("&");
                for (let i = 0; i < arr.length; i++) {
                    queryList.push({'name': arr[i].split("=")[0], 'value': arr[i].split('=')[1]});
                }
            }
            return queryList;
        },
        dateFormat(fmt, date) {
            let ret;
            const opt = {
                "Y+": date.getFullYear().toString(),        // 年
                "m+": (date.getMonth() + 1).toString(),     // 月
                "d+": date.getDate().toString(),            // 日
                "H+": date.getHours().toString(),           // 时
                "M+": date.getMinutes().toString(),         // 分
                "S+": date.getSeconds().toString()          // 秒
            };
            for (let k in opt) {
                ret = new RegExp("(" + k + ")").exec(fmt);
                if (ret) {
                    fmt = fmt.replace(ret[1], (ret[1].length === 1) ? (opt[k]) : (opt[k].padStart(ret[1].length, "0")))
                }
            }
            return fmt;
        },
        // Overview 根据 response code 加载不同的ClassName
        overviewRowClassName: function ({row, rowIndex}) {
            if (row.name === 'Response Code') {
                if (row.value >= 200 && row.value < 300) {
                    return 'success-row';
                } else if (row.value >= 300 && row.value < 400) {
                    return 'warning-row';
                } else if (row.value >= 400) {
                    return 'danger-row';
                }
            }
            return '';
        },
        // 分割线拖动
        dragDivider : function () {
            window.onload = function() {
                let controller = document.querySelector('#p-resize-controller');
                controller.onmousedown = function(e) {
                    let main = document.querySelector("#main");
                    main.className = 'p-select-none';
                    let urlList = document.querySelectorAll('#p-url-list li a span');
                    let startX = e.clientX;
                    let aside = document.querySelector('#p-aside');
                    let header = document.querySelector('#p-main>.el-tabs>.el-tabs__header');
                    let content = document.querySelector('#p-main>.el-tabs>.el-tabs__content');
                    let clientWidth = document.body.clientWidth;
                    document.onmousemove = function(event) {
                        let endX = event.clientX;
                        if (endX <= 300) {
                            return;
                        }
                        if (clientWidth - 20 < endX) {
                            return;
                        }
                        let moveLen = startX + (endX - startX);
                        controller.style.left = moveLen + 'px';
                        aside.style.width = moveLen + 'px';
                        header.style.left = moveLen + 'px';
                        content.style.left = moveLen + 'px';
                        if (!!urlList && moveLen > 580) {
                            for(let i = 0; i < urlList.length; i ++)
                            urlList[i].style.width = moveLen + 'px';
                        }
                    }
                    document.onmouseup = function() {
                        document.onmousemove = null;
                        document.onmouseup = null;
                        main.className = null;
                    }
                }
            }
        }
    },
    mounted() {
        this.dragDivider();
        this.timer = setTimeout(this.fetch, 200);
    },
    beforeDestroy() {
        clearInterval(this.timer);
    }
});