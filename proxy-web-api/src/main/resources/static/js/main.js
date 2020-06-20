new Vue({
    el: '#main',
    data: function () {
        return {
            currentFlowId: '',
            urlFilter: '',
            activeIndex: '1',
            hasResponse: false,
            contentsTabPosition: 'left',
            activeName: 'Overview',
            currentFlow: [],
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
            },
            treeProps: {
                label: function (data, node) {
                    return data.request.uri
                }
            }
        }
    },
    methods: {
        fetch() {
            const that = this;
            axios.get('/flow/fetch?host=' + this.search.host + '&port' + this.search.port + '&contentType=' + this.search.contentType + '&start=2020-06-11 07:54:00')
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
            this.currentFlow = [];
            this.currentFlow.push({
                'name': 'Time',
                'value': this.dateFormat('YYYY-mm-dd HH:MM:SS', new Date(data.request.timeCreated))
            });
            this.currentFlow.push({'name': 'Uri', 'value': data.request.uri});
            this.currentFlow.push({'name': 'Method', 'value': data.request.method});
            this.currentFlow.push({'name': 'Host', 'value': data.request.host});
            this.currentFlow.push({'name': 'Port', 'value': data.request.port});
            this.currentFlow.push({'name': 'HttpVersion', 'value': data.request.httpVersion});
            this.currentFlow.push({'name': 'Response Code', 'value': !data.response ? '-' : data.response.status});
            this.currentFlow.push({'name': 'Content-Type', 'value': !data.response ? '-' : data.response.contentType});

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
            this.hasResponse = true;
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
        }
    },
    mounted() {
        this.timer = setTimeout(this.fetch, 200);
    },
    beforeDestroy() {
        clearInterval(this.timer);
    }
});