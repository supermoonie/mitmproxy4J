const opts = {
    indent_size: '4',
    indent_char: ' ',
    max_preserve_newlines: '5',
    preserve_newlines: true,
    keep_array_indentation: false,
    break_chained_methods: false,
    indent_scripts: 'normal',
    brace_style: 'collapse',
    space_before_conditional: true,
    unescape_strings: false,
    jslint_happy: false,
    end_with_newline: false,
    wrap_line_length: '0',
    indent_inner_html: false,
    comma_first: false,
    e4x: false,
    indent_empty_lines: false
};
ace.config.set("basePath", "third/ace");
new Vue({
    el: '#main',
    data: function () {
        return {
            loading: false,
            tree: {
                nodeProps: {
                    label: 'url'
                }
            },
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
            flowTypeTabActive: 'Structure',
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
                    xml: '',
                    css: '',
                    js: '',
                    image: '',
                    plain: ''
                }
            },
            flow: {
                tree: {
                    all: [],
                    shown: []
                },
                list: {
                    all: [],
                    shown: []
                }
            },
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
                tabs: ['URL', 'Headers', 'Body'],
                activeTab: '0',
                requestDialogVisible: false,
                request: {
                    url: '',
                    method: 'GET',
                    contentType: 'none',
                    rawContentType: 'JSON'
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
                        editing: false,
                        editable: true
                    }]
                },
                form: {
                    select: '',
                    data: [{
                        name: '',
                        value: '',
                        editing: false
                    }]
                },
                multipart: {
                    select: '',
                    data: []
                },
                binary: {
                    target: undefined,
                    data: '',
                    text: '',
                    contentType: ''
                },
                rawBody: {
                    data: '',
                    type: 'JSON'
                }
            }
        }
    },
    methods: {
        clearClicked() {
            this.showFlows = [];
            this.currentFlow = undefined;
            this.currentFlowId = undefined;
        },
        searchClicked() {
            this.currentFlow = undefined;
            this.currentFlowId = undefined;
            this.fetchTreeFlow();
            this.fetchListFlow();
        },
        executeRequest() {
            const that = this;
            that.loading = true;
            let formData = new FormData();
            const method = this.edit.request.method;
            formData.append('method', method);
            formData.append('url', this.edit.request.url);
            let headers = [];
            !!this.edit.headers.data && this.edit.headers.data.forEach(header => {
                headers.push({name: header.name, value: header.value});
            });
            formData.append('headers', JSON.stringify(headers));
            if (['POST', 'PUT', 'PATCH'].indexOf(method) !== -1) {
                let textFormData = [];
                let contentType = this.edit.request.contentType;
                if (contentType === 'formData') {
                    this.edit.multipart.data.forEach(part => {
                        if (part.type === 'Text') {
                            textFormData.push({name: part.name.trim(), value: part.value.trim(), type: 'Text', fileName: '', contentType: part.contentType});
                        } else {
                            if (part.fileType === 'hex') {
                                textFormData.push({name: part.name.trim(), value: part.file, type: 'File', fileName: part.value.trim(), contentType: part.contentType});
                            } else {
                                formData.append(part.name.trim(), part.file);
                            }
                        }
                    });
                } else if (contentType === 'x-www-form-urlencoded') {
                    this.edit.form.data.forEach(form => {
                        textFormData.push({name: form.name.trim(), value: form.value.trim()});
                    });
                } else if (contentType === 'binary') {
                    if (typeof this.edit.binary.data === 'object') {
                        formData.append(this.edit.binary.data.name.trim(), this.edit.binary.data);
                    } else {
                        textFormData.push({name: '', value: this.edit.binary.data, contentType: this.edit.binary.contentType});
                    }
                } else if (contentType === 'raw') {
                    textFormData.push({name: '', value: codeEditor.getValue()});
                }
                formData.append('textFormData', JSON.stringify(textFormData));
                if (contentType === 'raw') {
                    contentType = this.edit.rawBody.type;
                }
                formData.append('requestContentType', contentType);
            }
            axios({
                method: 'post',
                url: '/http/execute',
                data: formData,
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            }).then(function (response) {
                setTimeout(() => {
                    that.loading = false;
                    that.edit.requestDialogVisible = false;
                }, 1000);
            }).catch(error => {
                that.loading = false;
                that.responseErrorHandler(error);
            });
        },
        uploadFileChange(multipart, event) {
            multipart.uploadFile = event.target.files[0];
            multipart.fileType = 'binary';
        },
        binaryUploadFileChange(event) {
            console.log(event);
            console.log(typeof event.target.files[0]);
            this.edit.binary.target = event.target;
            this.edit.binary.data = event.target.files[0];
            this.edit.binary.text = this.edit.binary.data.name + '  ' + this.edit.binary.data.size + ' bytes';
            this.edit.binary.contentType = this.edit.binary.data.type;
        },
        fetchListFlow() {
            const that = this;
            that.loading = true;
            axios({
                method: 'post',
                url: '/flow/list',
                data: {
                    host: '',
                    method: '',
                    start: '',
                    end: ''
                },
                transformRequest: [function (data) {
                    let ret = '';
                    for (let key in data) {
                        if (data.hasOwnProperty(key)) {
                            ret += encodeURIComponent(key) + '=' + encodeURIComponent(data[key]) + '&'
                        }
                    }
                    return ret;
                }],
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            }).then(function (response) {
                that.loading = false;
                that.flowResponseDataHandler(response.data, 'list');
            }).catch(error => {
                that.loading = false;
                that.responseErrorHandler(error);
            });
        },
        /**
         * 从服务器端拉取Flow数据
         */
        fetchTreeFlow() {
            const that = this;
            that.loading = true;
            axios({
                method: 'post',
                url: '/flow/tree',
                data: {
                    host: '',
                    method: '',
                    start: '',
                    end: ''
                },
                transformRequest: [function (data) {
                    let ret = '';
                    for (let key in data) {
                        if (data.hasOwnProperty(key)) {
                            ret += encodeURIComponent(key) + '=' + encodeURIComponent(data[key]) + '&'
                        }
                    }
                    return ret;
                }],
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            }).then(function (response) {
                that.loading = false;
                that.flowResponseDataHandler(response.data, 'tree');
            }).catch(error => {
                that.loading = false;
                that.responseErrorHandler(error);
            });
        },
        flowResponseDataHandler(data, field) {
            this.flow[field].all = data;
            this.doFilter(data, field);
        },
        responseErrorHandler(error) {
            this.$message({
                showClose: true,
                message: error,
                type: 'error'
            });
        },
        flowNodeClicked(data, node) {
            if (!data.type || (!!data.type && data.type === 'TARGET')) {
                if (!!this.currentFlowId && this.currentFlowId === data.id) {
                    return;
                }
                const that = this;
                that.loading = true;
                axios.get('/flow/detail/' + data.id)
                    .then(res => {
                        setTimeout(() => that.loading = false, 300);
                        that.handleUrlClicked(data.id, res.data, null)
                    })
                    .catch(error => {
                        console.error(error);
                        that.loading = false;
                        that.responseErrorHandler(error);
                    });
            }
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
        doFilter(data, field) {
            const that = this;
            this.flow[field].shown = data.filter(flow => {
                if (!that.urlFilter.trim()) {
                    return true;
                }
                return flow.url.indexOf(that.urlFilter.trim()) !== -1;
            })
        },
        /**
         * Flow列表过滤器
         */
        filter() {
            this.doFilter(this.flow.list.all, 'list');
            this.doFilter(this.flow.tree.all, 'tree');
        },
        /**
         * 请求方法改变事件处理
         */
        requestMethodChanged() {
            if ('POST' === this.edit.request.method || 'PUT' === this.edit.request.method) {
                this.edit.tabs = [].concat(['URL', 'Headers', 'Body']);
            } else {
                this.edit.tabs = [].concat(['URL', 'Headers']);
            }
        },
        initRequestEdit() {
            this.edit.request.url = '';
            this.edit.request.method = 'GET';
            this.edit.tabs = ['URL', 'Headers'];
            this.edit.query.data = [];
            this.edit.query.select = '';
            this.edit.headers = [];
            this.edit.request.contentType = 'none';
            this.edit.multipart.select = '';
            this.edit.multipart.data = [];
            this.edit.form.select = '';
            this.edit.form.data = [];
        },
        /**
         * 工具栏编辑图标点击事件处理
         */
        requestEditClicked() {
            this.initRequestEdit();
            if (!this.currentFlow || !this.currentFlow.request || !this.currentFlow.request.id) {
                this.edit.requestDialogVisible = true;
                this.edit.activeTab = '0';
                return;
            }
            this.edit.request.url = this.currentFlow.request.uri;
            this.edit.tabs = ['URL', 'Headers'];
            const entityMethod = ['POST', 'PUT', 'PATCH'];
            if (entityMethod.indexOf(this.currentFlow.request.method.toUpperCase()) !== -1) {
                this.edit.tabs = ['URL', 'Headers', 'Body'];
            }
            this.edit.request.method = this.currentFlow.request.method.toUpperCase();
            this.edit.query.data = Utils.unionListFrom(Utils.getQueryString(this.currentFlow.request.uri), {editing: false});
            let headers = [];
            this.currentFlow.requestHeaders.forEach(header => {
                header.editing = false;
                header.editable = ['content-length', 'content-type', 'host'].indexOf(header.name.toLowerCase()) === -1;
                headers.push(header);
            });
            this.edit.headers.data = headers;
            this.edit.headers.data = Utils.unionListFrom(this.currentFlow.requestHeaders, {editing: false});
            if (!!this.currentFlow.requestContent) {
                if (this.currentFlow.request.contentType.indexOf('multipart/form-data') !== -1) {
                    let text = CryptoJS.enc.Utf8.stringify(CryptoJS.enc.Hex.parse(this.currentFlow.requestContent));
                    let formData = Utils.parseMultipartData(this.currentFlow.request.contentType, text);
                    this.edit.multipart.data = Utils.unionListFrom(formData, {editing: false, uploadFile: ''});
                    this.edit.request.contentType = 'formData';
                } else if (this.currentFlow.request.contentType.indexOf('x-www-form-urlencoded') !== -1) {
                    let text = CryptoJS.enc.Utf8.stringify(CryptoJS.enc.Hex.parse(this.currentFlow.requestContent));
                    let queryList = [];
                    let search = new URLSearchParams(text);
                    for (let item of search) {
                        queryList.push({name: item[0], value: item[1], editing: false})
                    }
                    this.edit.form.data = queryList;
                    this.edit.request.contentType = 'x-www-form-urlencoded';
                } else if (this.currentFlow.request.contentType.indexOf('application/json') !== -1) {
                    this.edit.request.contentType = 'raw';
                    this.edit.rawBody.type = 'JSON';
                    this.edit.rawBody.data = js_beautify(CryptoJS.enc.Utf8.stringify(CryptoJS.enc.Hex.parse(this.currentFlow.requestContent)));
                } else if (this.currentFlow.request.contentType.indexOf('application/xml') !== -1) {
                    this.edit.request.contentType = 'raw';
                    this.edit.rawBody.type = 'XML';
                    this.edit.rawBody.data = html_beautify(CryptoJS.enc.Utf8.stringify(CryptoJS.enc.Hex.parse(this.currentFlow.requestContent)));
                } else if (this.currentFlow.request.contentType.indexOf('text/html') !== -1) {
                    this.edit.request.contentType = 'raw';
                    this.edit.rawBody.type = 'HTML';
                    this.edit.rawBody.data = html_beautify(CryptoJS.enc.Utf8.stringify(CryptoJS.enc.Hex.parse(this.currentFlow.requestContent)));
                } else if (this.currentFlow.request.contentType.indexOf('text/plain') !== -1) {
                    this.edit.request.contentType = 'raw';
                    this.edit.rawBody.type = 'Text';
                    this.edit.rawBody.data = CryptoJS.enc.Utf8.stringify(CryptoJS.enc.Hex.parse(this.currentFlow.requestContent));
                } else if (this.currentFlow.request.contentType.indexOf('application/javascript') !== -1) {
                    this.edit.request.contentType = 'raw';
                    this.edit.rawBody.type = 'JavaScript';
                    this.edit.rawBody.data = js_beautify(CryptoJS.enc.Utf8.stringify(CryptoJS.enc.Hex.parse(this.currentFlow.requestContent)));
                } else {
                    if (!!this.currentFlow.requestContent) {
                        this.edit.request.contentType = 'binary';
                        this.edit.binary.contentType = this.currentFlow.request.contentType;
                        this.edit.binary.data = this.currentFlow.requestContent;
                        this.edit.binary.text = CryptoJS.enc.Hex.parse(this.currentFlow.requestContent).sigBytes + ' bytes';
                    } else {
                        this.edit.request.contentType = 'none';
                    }
                }
            } else {
                this.edit.request.contentType = 'none';
            }
            this.edit.requestDialogVisible = true;
            this.edit.activeTab = '0';
        },
        rawContentTypeChanged(val) {
            console.log(val);
        },
        clearRequestBinaryClicked() {
            this.edit.binary.data = null;
            this.edit.binary.text = '';
            if (!!this.edit.binary.target) {
                this.edit.binary.target.value = '';
            }
        },
        requestUrlInput(value) {
            this.edit.query.data = Utils.getQueryString(value);
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
            let row;
            if ('multipart' === field) {
                row = {name: '', type: 'Text', value: '', uploadFile: '', editing: true, editable: true};
            } else {
                row = {name: '', value: '', editing: true, editable: true};
            }
            this.edit[field].data.push(row);
            this.edit[field].select = Utils.clone(row);
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
                if (row.name === '' && row.value === '') {
                    this.rowDeleteClicked(row, index, field);
                }
                return row.editing = !row.editing;
            }
            if (row.editing) {
                // OK
                if ('query' === field) {
                    row.name = this.edit[field].select.name.trim();
                    row.value = this.edit[field].select.value.trim();
                    let queryString = Utils.toQueryString(this.edit.query.data)
                    this.edit.request.url = Utils.truncateUrl(this.edit.request.url, '?') + '?' + queryString;
                } else if ('multipart' === field) {
                    const select = this.edit[field].select;
                    row.name = select.name.trim();
                    row.type = select.type;
                    row.fileType = select.fileType;
                    if (row.fileType === 'binary') {
                        row.file = select.uploadFile;
                        row.value = row.file.name.trim();
                    } else {
                        row.value = select.value.trim();
                    }
                    console.log(select);
                    console.log(row);
                } else {
                    // header
                    row.name = this.edit[field].select.name.trim();
                    row.value = this.edit[field].select.value.trim();
                }
                row.editing = false;
            } else {
                // Edit
                this.edit[field].data.forEach(data => {
                    data.editing = false;
                });
                this.edit[field].select = Utils.clone(row);
                row.editing = true;
                console.log(row);
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
            if ('query' === field) {
                let queryString = Utils.toQueryString(this.edit.query.data)
                if (!!queryString) {
                    this.edit.request.url = Utils.truncateUrl(this.edit.request.url, '?') + '?' + queryString;
                } else {
                    this.edit.request.url = Utils.truncateUrl(this.edit.request.url, '?');
                }
            }
        },
        /**
         * 处理Flow列表中元素点击事件
         *
         * @param id    flow.request.id
         * @param data  flow
         * @param event client event
         */
        handleUrlClicked(id, data, event) {
            this.currentFlow = data;
            this.currentFlowId = id;
            // Overview Tab
            this.current.overview = this.buildCurrentOverview(data);
            // Request Tab
            this.current.request.headers = data.requestHeaders;
            this.current.request.queryString = Utils.getQueryString(data.request.uri);
            let requestCookies = data.requestHeaders.filter(header => header.name === 'Cookie').map(header => {
                return header.value
            });
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
            const that = this;
            if (!data.response) {
                return;
            }
            raw = '<p>' + data.response.httpVersion + ' ' + data.response.status + '</p>';
            for (let i = 0; i < data.responseHeaders.length; i++) {
                let header = data.responseHeaders[i];
                raw = raw + '<p>' + header.name + ' : ' + header.value + '</p>';
            }
            if (data.responseContent) {
                let contentType = data.response.contentType.toLowerCase();
                if (contentType.indexOf('json') !== -1) {
                    let content = CryptoJS.enc.Utf8.stringify(CryptoJS.enc.Hex.parse(data.responseContent));
                    let clearedContent = Utils.replaceSpecialChar(content).replace(/\s{2}/g, '&nbsp;&nbsp;');
                    raw = raw + '<p></p><pre>' + clearedContent + '</pre><p></p>';
                    that.buildCurrentResponseJson(content);
                } else if (contentType.indexOf('text/xml') !== -1) {
                    let content = CryptoJS.enc.Utf8.stringify(CryptoJS.enc.Hex.parse(data.responseContent));
                    let clearedContent = Utils.replaceSpecialChar(content).replace(/\s{2}/g, '&nbsp;&nbsp;');
                    raw = raw + '<p></p><pre>' + clearedContent + '</pre><p></p>';
                    that.buildCurrentResponseXml(content);
                } else if (contentType.indexOf('text/html') !== -1) {
                    let content = CryptoJS.enc.Utf8.stringify(CryptoJS.enc.Hex.parse(data.responseContent));
                    let rawContent = Utils.replaceSpecialChar(content).replace(/\s{2}/g, '&nbsp;&nbsp;');
                    raw = raw + '<p></p><pre>' + rawContent + '</pre><p></p>';
                    this.buildCurrentResponseHtml(content);
                } else if (contentType.indexOf('text/css') !== -1) {
                    let content = CryptoJS.enc.Utf8.stringify(CryptoJS.enc.Hex.parse(data.responseContent));
                    raw = raw + '<p></p><pre>' + content + '</pre><p></p>';
                    this.buildCurrentResponseCss(content);
                } else if (contentType.indexOf('application/javascript') !== -1 || contentType.indexOf('application/x-javascript') !== -1) {
                    let content = CryptoJS.enc.Utf8.stringify(CryptoJS.enc.Hex.parse(data.responseContent));
                    let rawContent = Utils.replaceSpecialChar(content).replace(/\s{2}/g, '&nbsp;&nbsp;');
                    raw = raw + '<p></p><pre>' + rawContent + '</pre><p></p>';
                    this.buildCurrentResponseJavaScript(content);
                } else if (contentType.indexOf('image') !== -1) {
                    let content = Utils.wordsToString(CryptoJS.enc.Hex.parse(data.responseContent).words);
                    raw = raw + '<p></p><pre>' + content + '</pre><p></p>';
                    this.buildCurrentResponseImage(data.responseContent);
                } else if (contentType.indexOf('vnd.apple.mpegurl') !== -1) {
                    let content = CryptoJS.enc.Utf8.stringify(CryptoJS.enc.Hex.parse(data.responseContent));
                    raw = raw + '<p></p><pre>' + content + '</pre><p></p>';
                    this.responseTabs.push('Video');
                    this.videoUrl = data.request.uri;
                } else if (contentType.indexOf('video/mp2t') !== -1 || contentType.indexOf('application/octet-stream') !== -1) {
                    let content = Utils.wordsToString(CryptoJS.enc.Hex.parse(data.responseContent).words);
                    raw = raw + '<p></p><pre>' + content + '</pre><p></p>';
                    this.buildHexResponse(data.responseContent);
                } else {
                    let content = CryptoJS.enc.Utf8.stringify(CryptoJS.enc.Hex.parse(data.responseContent));
                    try {
                        JSON.parse(content);
                        let clearedContent = Utils.replaceSpecialChar(content).replace(/\s{2}/g, '&nbsp;&nbsp;');
                        raw = raw + '<p></p><pre>' + clearedContent + '</pre><p></p>';
                        this.buildCurrentResponseJson(content);
                    } catch (ignore) {
                        raw = raw + '<p></p><pre>' + content + '</pre><p></p>';
                        this.buildCurrentResponsePlain(content);
                    }
                }
            }
            return raw;
        },
        /**
         * 创建Response/Hex 中的内容
         */
        buildHexResponse(hexContent) {
            this.responseTabs.push('Hex');
            this.current.response.hex = hexContent;
        },
        /**
         * 创建Response/PLAIN 中的内容
         *
         * @param content   解码后的原文
         */
        buildCurrentResponsePlain(content) {
            this.responseTabs.push('Plain');
            this.current.response.plain = content;
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
            this.current.response.json = js_beautify(content);
        },
        buildCurrentResponseXml(content) {
            this.responseTabs.push('XML');
            this.current.response.xml = html_beautify(content);
        },
        /**
         * 数据创建Response/HTML 中的内容
         *
         * @param content  HTML Content
         */
        buildCurrentResponseHtml(content) {
            this.responseTabs.push('HTML');
            this.current.response.html = html_beautify(content, opts);
        },
        /**
         * 数据创建Response/JavaScript 中的内容
         *
         * @param content  JavaScript Content
         */
        buildCurrentResponseCss(content) {
            this.responseTabs.push('CSS');
            content = css_beautify(content, opts);
            this.current.response.css = content;
        },
        /**
         * 数据创建Response/JavaScript 中的内容
         *
         * @param content  JavaScript Content
         */
        buildCurrentResponseJavaScript(content) {
            this.responseTabs.push('JavaScript');
            this.current.response.js = js_beautify(content, opts);
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
                    content = Utils.replaceSpecialChar(content).replace(/\s{2}/g, '&nbsp;&nbsp;');
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
                const listContainer = document.querySelector('#p-list-container')
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
                    listContainer.style.overflowY = 'hidden';
                    let moveLen = startX + (endX - startX);
                    controller.style.left = moveLen + 'px';
                    aside.style.width = moveLen + 'px';
                    header.style.left = moveLen + 'px';
                    content.style.left = moveLen + 'px';
                    // if (!!urlList && moveLen > 580) {
                    //     for (let i = 0; i < urlList.length; i++)
                    //         urlList[i].style.width = moveLen + 'px';
                    // }
                }
                document.onmouseup = function () {
                    document.onmousemove = null;
                    document.onmouseup = null;
                    main.className = null;
                    listContainer.style.overflowY = 'scroll';
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
            listContainer.style.height = offsetHeight - 152 + 'px';
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
        this.timer = setTimeout(() => {
            that.fetchTreeFlow();
            that.fetchListFlow();
        }, 200);
    },
    beforeDestroy() {
        clearInterval(this.timer);
    }
});
// Vue.directive('highlight', function (el) {
//     let blocks = el.querySelectorAll('pre code');
//     blocks.forEach((block) => {
//         hljs.highlightBlock(block)
//     });
// });
Vue.directive('readOnlyEditor', {
    editor: undefined,
    inserted: function (el, binding, vNode) {
        this.editor = ace.edit("readOnlyEditor");
        this.editor.setTheme("ace/theme/chrome");
        this.editor.getSession().setMode("ace/mode/" + binding.value.mode);
        this.editor.setOptions({
            enableBasicAutocompletion: true,
            enableLiveAutocompletion: true
        });
        this.editor.setValue(binding.value.data, 1);
        this.editor.setReadOnly(binding.value.readOnly);
    },
    componentUpdated: function (el, binding, vNode) {
        this.editor.getSession().setMode("ace/mode/" + binding.value.mode);
        this.editor.setValue(binding.value.data, -1);
        this.editor.setReadOnly(binding.value.readOnly);
    }
});
let codeEditor = undefined;
Vue.directive('editor', {
    inserted: function (el, binding, vNode) {
        codeEditor = ace.edit("editBodyEditor");
        codeEditor.setTheme("ace/theme/chrome");
        codeEditor.getSession().setMode("ace/mode/" + binding.value.mode);
        codeEditor.setOptions({
            enableBasicAutocompletion: true,
            enableLiveAutocompletion: true
        });
        codeEditor.setValue(binding.value.data, 1);
        codeEditor.setReadOnly(binding.value.readOnly);
    },
    componentUpdated: function (el, binding, vNode) {
        codeEditor.getSession().setMode("ace/mode/" + binding.value.mode);
        codeEditor.setValue(binding.value.data, -1);
        codeEditor.setReadOnly(binding.value.readOnly);
    }
});
Vue.directive('focus', {
    inserted: function (el) {
        el.children[0].focus();
    }
});