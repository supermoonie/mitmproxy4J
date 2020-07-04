new Vue({
    el: '#main',
    data: function () {
        return {
            currentFlowId: '',
            currentFlow: {
                request: {
                    contentId: undefined,
                    contentType: undefined,
                    host: undefined,
                    httpVersion: undefined,
                    id: undefined,
                    method: undefined,
                    port: undefined,
                    timeCreated: undefined,
                    uri: undefined
                },
                requestContent: undefined,
                requestHeaders: [{
                    name: undefined,
                    value: undefined
                }],
                response: {
                    contentId: undefined,
                    contentType: undefined,
                    httpVersion: undefined,
                    id: undefined,
                    requestId: undefined,
                    status: undefined,
                    timeCreated: undefined,
                },
                responseContent: undefined,
                responseHeaders: [{
                    name: undefined,
                    value: undefined
                }]
            },
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
            videoUrl: '',
            videoDialogVisible: false,
            edit: {
                tabs: ['URL', 'Headers', 'Text', 'Form'],
                activeTab: '0',
                requestDialogVisible: false,
                request: {
                    url: '',
                    method: 'GET',
                    allowMethods: ['GET', 'POST', 'PUT', 'DELETE', 'HEAD', 'TRACE']
                },
                query: {
                    select: '',
                    data: [{
                        name: '',
                        value: '',
                        editing: false
                    }]
                },
                headers: {
                    select: '',
                    data: [{
                        name: '',
                        value: '',
                        editing: false
                    }]
                },
                text: ''
            }
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
                }).catch(function (error) {
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
         * 请求方法改变时间处理
         */
        requestMethodChanged() {
            if ('GET' === this.edit.request.method) {
                this.edit.tabs = [].concat(['URL', 'Headers', 'Text']);
            } else {
                this.edit.tabs = [].concat(['URL', 'Headers', 'Text', 'Form']);
                this.edit.request.allowMethods = [].concat(['POST', 'PUT', 'DELETE', 'HEAD', 'TRACE']);
            }
        },
        /**
         * 工具栏编辑图标点击事件处理
         */
        requestEditClicked() {
            if (!this.currentFlow) {
                this.$message({
                    message: 'Please select one request!',
                    type: 'warning'
                });
                return;
            }
            this.edit.request.url = Utils.truncate(this.currentFlow.request.uri, '?');
            this.edit.request.allowMethods = [];
            if (this.currentFlow.request.method.toUpperCase() === 'GET') {
                this.edit.request.allowMethods = ['GET', 'POST', 'PUT', 'DELETE', 'HEAD'];
                this.edit.tabs = ['URL', 'Headers', 'Text'];
            } else {
                this.edit.request.allowMethods = ['POST', 'PUT', 'DELETE', 'HEAD', 'TRACE'];
                this.edit.tabs = ['URL', 'Headers', 'Text', 'Form'];
            }
            this.edit.request.method = this.currentFlow.request.method.toUpperCase();
            this.edit.query.data = Utils.unionListFrom(Utils.getQueryString(this.currentFlow.request.uri), {editing: false});
            this.edit.headers.data = Utils.unionListFrom(this.currentFlow.requestHeaders, {editing: false});
            this.text = '';
            if (!!this.currentFlow.requestContent) {
                this.edit.text = Utils.wordsToString(CryptoJS.enc.Hex.parse(this.currentFlow.requestContent).words);
                if (this.currentFlow.request.contentType.indexOf('multipart/form-data') !== -1) {
                    let boundary = this.currentFlow.request.contentType.split('boundary=')[1];
                    let dataArr = this.edit.text.split(/[\r\n]/);
                }
            }
            this.edit.requestDialogVisible = true;
        },
        /**
         * 增加一行事件处理
         *
         * @param field this.edit 中的字段
         */
        addClicked(field) {
            this.edit[field].data.forEach(data => {
                data.editing = false;
            });
            let row = {name: '', value: '', editing: true};
            this.edit[field].data.push(row);
            this.edit[field].select = row;
        },
        /**
         * 表格行编辑事件处理
         *
         * @param row   行数据
         * @param index 行号
         * @param flag  true ? 编辑操作 : 取消操作
         * @param field this.edit 中的字段
         */
        rowEditClicked(row, index, flag, field) {
            console.log(row);
            //是否是取消操作
            if (!flag) {
                return row.editing = !row.editing;
            }
            if (row.editing) {
                row.name = this.edit[field].select.name;
                row.value = this.edit[field].select.value;
                row.editing = false;
            } else {
                this.edit[field].select = JSON.parse(JSON.stringify(row));
                row.editing = true;
            }
        },
        /**
         * 表格行删除事件处理
         *
         * @param row   行数据
         * @param index 行索引
         * @param field this.edit 中的字段
         */
        rowDeleteClicked(row, index, field) {
            this.edit[field].data.splice(index, 1);
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
            this.currentFlow = data;
            this.currentFlowId = id;
            // Overview Tab
            this.current.overview = this.buildCurrentOverview(data);
            // Request Tab
            this.current.request.headers = data.requestHeaders;
            this.current.request.queryString = Utils.getQueryString(data.request.uri);
            let requestCookies = data.requestHeaders.filter(header => header.name === 'Cookie');
            this.current.request.cookies = Utils.parseRequestCookie(requestCookies);
            this.current.request.raw = this.buildCurrentRequestRaw(data);
            // Response Tab
            this.current.response.headers = !!data.responseHeaders ? data.responseHeaders : [];
            this.current.response.cookies = Utils.parseResponseCookie(data.responseHeaders);
            let beforeLen = this.responseTabs.length;
            this.responseTabs = [].concat(this.baseResponseTabs);
            this.current.response.raw = this.buildCurrentResponseRaw(data);
            let afterLen = this.responseTabs.length;
            if (beforeLen !== afterLen) {
                document.querySelector('#pane-Response #tab-0').click()
            }
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
                let type = /.*\/(.*);?.*/.exec(contentType)[1].toLowerCase().trim();
                if ('json' === type) {
                    let content = CryptoJS.enc.Utf8.stringify(CryptoJS.enc.Hex.parse(data.responseContent));
                    let clearedContent = Utils.replaceSpecialChar(content).replace(/\s{2}/g, '&nbsp;&nbsp;');
                    raw = raw + '<p></p><pre>' + clearedContent + '</pre><p></p>';
                    that.buildCurrentResponseJson(content);
                } else if ('html' === type) {
                    let content = CryptoJS.enc.Utf8.stringify(CryptoJS.enc.Hex.parse(data.responseContent));
                    content = Utils.replaceSpecialChar(content).replace(/\s{2}/g, '&nbsp;&nbsp;');
                    raw = raw + '<p></p><pre>' + content + '</pre><p></p>';
                    this.buildCurrentResponseHtml(content);
                } else if (contentType.indexOf('image') !== -1) {
                    let content = Utils.wordsToString(CryptoJS.enc.Hex.parse(data.responseContent).words);
                    raw = raw + '<p></p><pre>' + content + '</pre><p></p>';
                    this.buildCurrentResponseImage(data.responseContent);
                } else if ('vnd.apple.mpegurl' === type) {
                    let content = CryptoJS.enc.Utf8.stringify(CryptoJS.enc.Hex.parse(data.responseContent));
                    raw = raw + '<p></p><pre>' + content + '</pre><p></p>';
                    this.responseTabs.push('Video');
                    this.videoUrl = data.request.uri;
                } else {
                    let content = CryptoJS.enc.Utf8.stringify(CryptoJS.enc.Hex.parse(data.responseContent));
                    console.log(content);
                    try {
                        JSON.parse(content);
                        let clearedContent = Utils.replaceSpecialChar(content).replace(/\s{2}/g, '&nbsp;&nbsp;');
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
         * 创建Response/PLAIN 中的内容
         *
         * @param content   解码后的原文
         */
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
            this.current.response.json = '<pre class="JSON" style="margin: 0;"><code>' + JSON.pretty(content) + '</code></pre>';
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
                'value': Utils.dateFormat('YYYY-mm-dd HH:MM:SS', new Date(data.request.timeCreated))
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
            for (let i = 0; i < data.requestHeaders.length; i++) {
                let header = data.requestHeaders[i];
                raw = raw + '<p>' + header.name + ' : ' + header.value + '</p>';
            }
            if (data.requestContent) {
                let content = Utils.wordsToString(CryptoJS.enc.Hex.parse(data.requestContent).words);
                let requestContentType = data.request.contentType;
                if (requestContentType.indexOf('multipart/form-data') !== -1) {
                    raw = raw + '<p></p>';
                    let contents = content.split('\r\n');
                    for (let i = 0; i < contents.length; i++) {
                        raw = raw + '<p>' + Utils.replaceSpecialChar(contents[i]) + '</p>'
                    }
                } else {
                    raw = raw + '<p></p><p>' + content + '</p>'
                }
            }
            return raw;
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
            // 113: p-list-container 距离顶部的高度，即 header.height + aside.search.height
            listContainer.style.height = offsetHeight - 113 + 'px';
        },
        /**
         * 窗口大小变化时自动调整元素标签高度
         */
        autoAdjustWhenWindowResize: function () {
            let that = this;
            window.onresize = function () {
                that.calAdjust();
            }
        },
        /**
         * 当视频播放器Dialog打开时初始化视频播放器 & 切换视频 & 播放视频
         */
        videoDialogOpened() {
            this.initialPlayer();
            if (this.dp.options.video.url !== this.videoUrl) {
                this.dp.options.video.url = this.videoUrl;
                this.dp.switchVideo({url: this.videoUrl}, null);
            }
            let that = this;
            setTimeout(function () {
                that.dp.play();
            }, 1000)
        },
        /**
         * 初始化视频播放器
         */
        initialPlayer() {
            !this.dp && (this.dp = new DPlayer({
                container: document.getElementById('dPlayer'),
                loop: false,
                autoplay: true,
                video: {
                    url: '',
                    type: 'hls'
                }
            }));
        }
    },
    mounted() {
        let that = this;
        window.onload = function () {
            that.calAdjust();
            that.dragDivider();
            that.autoAdjustWhenWindowResize();
            axios.defaults.baseURL = "http://127.0.1:8866";
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