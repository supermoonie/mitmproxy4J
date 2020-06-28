new Vue({
    el: '#main',
    data: function () {
        return {
            currentFlowId: '',
            urlFilter: '',
            activeName: 'Overview',
            current: {
                overview: [],
                request: {
                    headers: [],
                    queryString: [],
                    cookies: [],
                    raw: ''
                },
                response: {
                    headers: [],
                    cookies: [],
                    raw: '',
                    json: '',
                    html: '',
                    image: '',
                    plain: ''
                }
            },
            // 服务器端返回的所有Flow
            allFlows: [],
            // 过滤后用于展示的Flow
            showFlows: [],
            contentTypeList: [
                'application/json', 'multipart/form-data', 'application/x-www-form-urlencoded', 'application/xhtml+xml',
                'text/html', 'text/plain', 'text/xml', 'image/gif', 'image/jpeg', 'image/png',
                'application/xml', 'application/atom+xml', 'application/pdf', 'application/msword', 'application/octet-stream',
            ],
            timer: '',
            search: {
                host: '',
                port: '',
                contentType: ''
            },
            baseResponseTabs: ['Headers', 'Cookies', 'Raw'],
            responseTabs: ['Headers', 'Cookies', 'Raw'],
            activeResponsePanel: '1',
            dp: '',
            videoUrl: ''
        }
    },
    methods: {
        /**
         * 从服务器端拉取Flow数据
         */
        fetchFlow() {
            const that = this;
            axios.get('/flow/fetch?host=' + this.search.host + '&port' + this.search.port + '&contentType=' + this.search.contentType + '&start=2020-06-11 07:54:00')
                .then(function (response) {
                    that.allFlows = response.data;
                    that.showFlows = [];
                    for (let i = 0; i < that.allFlows.length; i++) {
                        if (that.allFlows[i].request.uri.indexOf(that.urlFilter) !== -1) {
                            that.showFlows.push(that.allFlows[i]);
                        }
                    }
                })
                .catch(function (error) {
                    that.$message({
                        showClose: true,
                        message: error,
                        type: 'error'
                    });
                });
        },
        /**
         * 处理菜单栏选择事件
         *
         * @param key   菜单的唯一索引key
         * @param keyPath   菜单的路径
         */
        handleSelect(key, keyPath) {
            console.log(key, keyPath);
        },
        /**
         * Flow列表过滤器
         */
        filter() {
            this.showFlows = [];
            for (let i = 0; i < this.allFlows.length; i++) {
                if (this.allFlows[i].request.uri.indexOf(this.urlFilter) !== -1) {
                    this.showFlows.push(this.allFlows[i]);
                }
            }
        },
        /**
         * 清除Flow列表
         */
        clear() {
            this.showFlows = [];
        },
        /**
         * 处理Flow列表中元素点击事件
         *
         * @param id    flow.request.id
         * @param data  flow
         * @param event client event
         */
        handleUrlClicked(id, data, event) {
            console.log(data);
            this.currentFlowId = id;
            // Overview Tab
            this.current.overview = this.buildCurrentOverview(data);
            // Request Tab
            this.current.request.headers = data.requestHeaders;
            this.current.request.queryString = this.getQueryString(data.request.uri);
            let requestCookies = data.requestHeaders.filter(header => header.name === 'Cookie');
            this.current.request.cookies = this.parseRequestCookie(requestCookies);
            this.current.request.raw = this.buildCurrentRequestRaw(data);
            // Response Tab
            this.current.response.headers = !!data.responseHeaders ? data.responseHeaders : [];
            this.current.response.cookies = this.parseResponseCookie(data);
            let beforeLen = this.responseTabs.length;
            this.responseTabs = [].concat(this.baseResponseTabs);
            this.current.response.raw = this.buildCurrentResponseRaw(data);
            let afterLen = this.responseTabs.length;
            if (beforeLen !== afterLen) {
                document.querySelector('#pane-Response #tab-0').click()
            }
            this.responseTabClicked({label: 'Video'});
        },
        /**
         * 根据Flow数据创建Response/Raw 中的内容
         *
         * @param data  Flow
         * @returns string  Raw Content
         */
        buildCurrentResponseRaw(data) {
            let raw = '';
            if (data.responseContent) {
                const that = this;
                raw = '<p>' + data.response.httpVersion + ' ' + data.response.status + '</p>';
                for (let i = 0; i < data.responseHeaders.length; i++) {
                    let header = data.responseHeaders[i];
                    raw = raw + '<p>' + header.name + ' : ' + header.value + '</p>';
                }
                let contentType = data.response.contentType;
                let type = contentType.split(';')[0].split('/')[1].toLowerCase().trim();
                if ('json' === type) {
                    let content = CryptoJS.enc.Utf8.stringify(CryptoJS.enc.Hex.parse(data.responseContent));
                    let clearedContent = this.replaceSpecialChar(content).replace(/\s{2}/g, '&nbsp;&nbsp;');
                    raw = raw + '<p></p><pre>' + clearedContent + '</pre><p></p>';
                    that.buildCurrentResponseJson(content);
                } else if ('html' === type) {
                    let content = CryptoJS.enc.Utf8.stringify(CryptoJS.enc.Hex.parse(data.responseContent));
                    content = this.replaceSpecialChar(content).replace(/\s{2}/g, '&nbsp;&nbsp;');
                    raw = raw + '<p></p><pre>' + content + '</pre><p></p>';
                    this.buildCurrentResponseHtml(content);
                } else if (contentType.indexOf('image') !== -1) {
                    let content = this.byteArrayToString(this.wordsToByteArray(CryptoJS.enc.Hex.parse(data.responseContent).words));
                    raw = raw + '<p></p><pre>' + content + '</pre><p></p>';
                    this.buildCurrentResponseImage(data.responseContent);
                } else if ('vnd.apple.mpegurl' === type) {
                    let content = CryptoJS.enc.Utf8.stringify(CryptoJS.enc.Hex.parse(data.responseContent));
                    raw = raw + '<p></p><pre>' + content + '</pre><p></p>';
                    this.responseTabs.push('Video');
                    this.videoUrl = data.request.uri;
                } else  {
                    let content = CryptoJS.enc.Utf8.stringify(CryptoJS.enc.Hex.parse(data.responseContent));
                    console.log(content);
                    try {
                        JSON.parse(content);
                        let clearedContent = this.replaceSpecialChar(content).replace(/\s{2}/g, '&nbsp;&nbsp;');
                        raw = raw + '<p></p><pre>' + clearedContent + '</pre><p></p>';
                        this.buildCurrentResponseJson(content);
                    } catch (ignore) {
                        this.buildCurrentResponsePlain(content);
                    }
                }
            }
            return raw;
        },
        /**
         * 处理Response 中的Tab 的点击事件，主要用于创建视频播放器
         *
         * @param tab   tab
         */
        responseTabClicked(tab) {
            if (tab.label === 'Video' && this.videoUrl && !!document.getElementById('dplayer')) {
                if (!this.dp) {
                    this.dp = new DPlayer({
                        container: document.getElementById('dplayer'),
                        loop: false,
                        video: {
                            url: this.videoUrl,
                            type: 'hls'
                        }
                    });
                    console.log('created dplayer url: ' + this.videoUrl);
                } else {
                    console.log('current video url: ' + this.dp.options.video.url);
                    if (this.dp.options.video.url !== this.videoUrl) {
                        this.dp.options.video.url = this.videoUrl;
                        this.dp.switchVideo({url: this.videoUrl}, null);
                        console.log('switch video: ' + this.videoUrl);
                    }
                }
            } else {
                if (this.dp) {
                    this.dp.pause();
                }
            }
        },
        buildCurrentResponsePlain(content) {
            this.responseTabs.push('Plain');
            this.current.response.plain = '<pre class="Plain" style="margin: 0;"><code>' + content + '</code></pre>';
        },
        /**
         * 创建Response/IMAGE 中的内容
         *
         * @param content   hex image
         */
        buildCurrentResponseImage(content) {
            this.responseTabs.push('Image');
            let base64Image = CryptoJS.enc.Base64.stringify(CryptoJS.enc.Hex.parse(content));
            this.current.response.image = '<img src="data:image/png;base64, ' + base64Image + '" />';
        },
        /**
         * 创建Response/JSON 中的内容
         *
         * @param content  JSON Content
         */
        buildCurrentResponseJson(content) {
            this.responseTabs.push('JSON');
            content = content.replace(/ufffd/g, '\\----')
            let json = JSON.stringify(JSON.parse(content), null, 2);
            json = json.replace(/\\----/g, 'ufffd');
            this.current.response.json = '<pre class="JSON" style="margin: 0;"><code>' + json + '</code></pre>';
        },
        /**
         * 数据创建Response/HTML 中的内容
         *
         * @param content  HTML Content
         */
        buildCurrentResponseHtml(content) {
            this.responseTabs.push('HTML');
            this.current.response.html = '<pre class="HTML" style="margin: 0;"><code>' + content + '</code></pre>';
        },
        /**
         * 根据Flow数据创建Overview 中的内容
         *
         * @param data  Flow
         * @returns [{}] Overview Table
         */
        buildCurrentOverview(data) {
            let overview = [];
            overview.push({
                'name': 'Time',
                'value': this.dateFormat('YYYY-mm-dd HH:MM:SS', new Date(data.request.timeCreated))
            });
            overview.push({'name': 'Uri', 'value': data.request.uri});
            overview.push({'name': 'Method', 'value': data.request.method});
            overview.push({'name': 'Host', 'value': data.request.host});
            overview.push({'name': 'Port', 'value': data.request.port});
            overview.push({'name': 'HttpVersion', 'value': data.request.httpVersion});
            overview.push({'name': 'Response Code', 'value': !data.response ? '-' : data.response.status});
            overview.push({'name': 'Content-Type', 'value': !data.response ? '-' : data.response.contentType});
            return overview;
        },
        /**
         * 通过Flow数据创建Request/Raw 中的内容
         *
         * @param data  Flow
         * @returns string raw
         */
        buildCurrentRequestRaw(data) {
            let raw = '<p>' + data.request.method + ' ' + data.request.uri + ' ' + data.request.httpVersion + '</p>';
            let requestContentType;
            for (let i = 0; i < data.requestHeaders.length; i++) {
                let header = data.requestHeaders[i];
                if (header.name === 'Content-Type') {
                    requestContentType = header.value.trim();
                }
                raw = raw + '<p>' + header.name + ' : ' + header.value + '</p>';
            }
            if (data.requestContent) {
                let content = this.byteArrayToString(this.wordsToByteArray(CryptoJS.enc.Hex.parse(data.requestContent).words));
                if (requestContentType.indexOf('multipart/form-data') !== -1) {
                    raw = raw + '<p></p>';
                    let contents = content.split('\r\n');
                    for (let i = 0; i < contents.length; i++) {
                        raw = raw + '<p>' + this.replaceSpecialChar(contents[i]) + '</p>'
                    }
                } else {
                    raw = raw + '<p></p><p>' + content + '</p>'
                }
            }
            return raw;
        },
        /**
         * 替换文本中的特殊字符，用于v-html 显示HTML 源码
         *
         * @param text 替换前的文本
         * @returns string 替换后的文本
         */
        replaceSpecialChar(text) {
            return text.replace(/&/g, '&amp;')
                .replace(/</g, '&lt;')
                .replace(/>/g, '&gt;')
                .replace(/"/g, '&quot;')
                .replace(/'/g, '&#39;')
                .replace(/\n/g, '<br>');
        },
        /**
         * Overview Table row 根据 response code 加载不同的ClassName
         *
         * @param row   行数据
         * @param rowIndex  行索引
         * @returns string className
         */
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
        /**
         * 解析请求头中的Cookie
         *
         * @param requestCookies 请求头中的Cookie
         * @returns [{}] cookie list
         */
        parseRequestCookie(requestCookies) {
            let cookies = [];
            if (requestCookies.length !== 0) {
                let cookie = requestCookies[0].value;
                let cookieFieldList = cookie.split(';');
                for (let i = 0; i < cookieFieldList.length; i++) {
                    cookies.push({
                        'name': cookieFieldList[i].split('=')[0],
                        'value': cookieFieldList[i].split('=')[1].trim()
                    })
                }
            }
            return cookies;
        },
        /**
         * 解析响应头中的 Set-Cookie
         *
         * @param data  Flow数据
         * @returns [{}]    cookie list
         */
        parseResponseCookie(data) {
            let cookies = [];
            if (!!data.responseHeaders) {
                let responseCookies = data.responseHeaders.filter(header => header.name === 'Set-Cookie');
                if (responseCookies.length !== 0) {
                    for (let i = 0; i < responseCookies.length; i++) {
                        let cookieText = responseCookies[i].value;
                        let cookieFieldList = cookieText.split(';');
                        let cookie = {};
                        for (let i = 0; i < cookieFieldList.length; i++) {
                            let field = cookieFieldList[i].split('=')[0].toLowerCase().trim();
                            let value = cookieFieldList[i].split('=')[1];
                            if (field === 'httponly') {
                                cookie['HTTPOnly'] = 'true';
                            } else if (field === 'secure') {
                                cookie['secure'] = 'true';
                            } else if (field === 'max-age') {
                                cookie['Max-Age'] = value;
                            } else if (field === 'expires') {
                                cookie['expires'] = this.dateFormat('YYYY-mm-dd HH:MM:SS', new Date(value))
                            } else if (field === 'path') {
                                cookie['path'] = value;
                            } else if (field === 'samesite') {
                                cookie['SameSite'] = value;
                            } else {
                                cookie['name'] = cookieFieldList[i].split('=')[0];
                                cookie['value'] = value;
                            }
                        }
                        cookies.push(cookie);
                    }
                }
            }
            return cookies;
        },
        /**
         * 从uri中获取queryString
         *
         * @param uri   request.uri
         * @returns [{}]    queryString
         */
        getQueryString(uri) {
            let queryList = [];
            if (uri.indexOf('?') !== -1) {
                let query = uri.split('?')[1];
                let arr = query.split('&');
                for (let i = 0; i < arr.length; i++) {
                    queryList.push({'name': arr[i].split('=')[0], 'value': arr[i].split('=')[1]});
                }
            }
            return queryList;
        },
        wordsToByteArray(words) {
            let byteArray = [], word, i, j;
            for (i = 0; i < words.length; ++i) {
                word = words[i];
                for (j = 3; j >= 0; --j) {
                    byteArray.push((word >> 8 * j) & 0xFF);
                }
            }
            return byteArray;
        },
        byteArrayToString(byteArray) {
            let binary = '';
            let bytes = new Uint8Array(byteArray);
            let len = bytes.byteLength;
            for (let i = 0; i < len; i++) {
                binary += String.fromCharCode(bytes[i]);
            }
            return binary;
        },
        /**
         * 日期格式化
         *
         * @param fmt   pattern
         * @param date  日期
         * @returns {*}
         */
        dateFormat(fmt, date) {
            let ret;
            const opt = {
                'Y+': date.getFullYear().toString(),        // 年
                'm+': (date.getMonth() + 1).toString(),     // 月
                'd+': date.getDate().toString(),            // 日
                'H+': date.getHours().toString(),           // 时
                'M+': date.getMinutes().toString(),         // 分
                'S+': date.getSeconds().toString()          // 秒
            };
            for (let k in opt) {
                ret = new RegExp('(' + k + ')').exec(fmt);
                if (ret) {
                    fmt = fmt.replace(ret[1], (ret[1].length === 1) ? (opt[k]) : (opt[k].padStart(ret[1].length, '0')))
                }
            }
            return fmt;
        },
        /**
         * 分割线拖动
         */
        dragDivider: function () {
            let controller = document.querySelector('#p-resize-controller');
            controller.onmousedown = function (e) {
                let main = document.querySelector('#main');
                main.className = 'p-select-none';
                let urlList = document.querySelectorAll('#p-url-list li a span');
                let startX = e.clientX;
                let aside = document.querySelector('#p-aside');
                let header = document.querySelector('#p-main>.el-tabs>.el-tabs__header');
                let content = document.querySelector('#p-main>.el-tabs>.el-tabs__content');
                let clientWidth = document.body.clientWidth;
                document.onmousemove = function (event) {
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
                        for (let i = 0; i < urlList.length; i++)
                            urlList[i].style.width = moveLen + 'px';
                    }
                }
                document.onmouseup = function () {
                    document.onmousemove = null;
                    document.onmouseup = null;
                    main.className = null;
                }
            }
        },
        /**
         * 通过计算调整元素标签的高度
         */
        calAdjust: function () {
            let offsetHeight = document.body.offsetHeight;
            let listContainer = document.getElementById('p-list-container');
            listContainer.style.height = offsetHeight - 108 + 'px';
        },
        /**
         * 窗口大小变化时自动调整元素标签高度
         */
        autoAdjustWhenWindowResize: function () {
            let that = this;
            window.onresize = function () {
                that.calAdjust();
            }
        }
    },
    mounted() {
        let that = this;
        window.onload = function () {
            that.calAdjust();
            that.dragDivider();
            that.autoAdjustWhenWindowResize();
            axios.defaults.baseURL="http://127.0.1:8866";
        }
        this.timer = setTimeout(this.fetchFlow, 200);
    },
    beforeDestroy() {
        clearInterval(this.timer);
    }
});
Vue.directive('highlight', function (el) {
    let blocks = el.querySelectorAll('pre code');
    blocks.forEach((block) => {
        hljs.highlightBlock(block)
    })
});