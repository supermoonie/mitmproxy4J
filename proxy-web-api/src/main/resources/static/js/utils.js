(function (win) {
    let Utils = {};
    const _sub = function (str) {
        return [...str.matchAll(/name="([^\s]+)"/g)].map(item => item[1])
    };
    Utils.clone = function (obj) {
        let o;
        if (typeof obj === "object") {
            if (obj === null) {
                o = null;
            } else if (obj instanceof Array) {
                o = [];
                for (let i = 0; i < obj.length; i++) {
                    o.push(clone(obj[i]));
                }
            } else {
                o = {}
                for (let j in obj) {
                    if (obj.hasOwnProperty(j)) {
                        o[j] = Utils.clone(obj[j])
                    }
                }
            }
        } else {
            o = obj;
        }
        return o;
    };
    /**
     * 解析multipart
     *
     * @param contentType   request.contentType
     * @param text          字符串类型requestContent
     * @returns [{}]        multipart
     */
    Utils.parseMultipartData = (contentType, text) => {
        let result = [];
        let boundary = contentType.split('boundary=')[1];
        let parts = text.split(new RegExp('\\S*' + boundary + '[\r\n]*'));
        for (let part of parts) {
            if (!part || part.indexOf('Content') === -1) {
                continue;
            }
            let formData = {
                name: '',
                value: '',
                fileName: '',
                contentType: '',
                file: '',
                fileType: 'hex',
                type: ''
            };
            if (part.indexOf('Content-Type:') !== -1) {
                formData.contentType = part.split('Content-Type: ')[1].split('\n')[0];
            } else {
                continue;
            }
            let contentDispositionMatcher = _sub(part);
            if (!!contentDispositionMatcher) {
                formData.name = contentDispositionMatcher[0];
                if (contentDispositionMatcher.length === 2) {
                    // 二进制格式的formData
                    formData.value = contentDispositionMatcher[1];
                    if (part.indexOf('\r\n\r\n') !== -1) {
                        let value = part.substring(part.indexOf('\r\n\r\n') + 4, part.length - 1);
                        formData.file = CryptoJS.enc.Hex.stringify(CryptoJS.enc.Latin1.parse(value));
                        formData.type = 'File';
                    }
                } else {
                    let encoding = part.split("Content-Transfer-Encoding: ")[1].split('\r')[0];
                    if (encoding === 'binary') {
                        formData.type = 'File';
                        if (part.indexOf('\r\n\r\n') !== -1) {
                            let value = part.substring(part.indexOf('\r\n\r\n') + 4, part.length - 1);
                            let wordArray = CryptoJS.enc.Latin1.parse(value);
                            formData.file = CryptoJS.enc.Hex.stringify(wordArray);
                            formData.value = wordArray.sigBytes + ' bytes';
                        }
                    } else {
                        // 文本格式的formData
                        if (part.indexOf('\r\n\r\n') !== -1) {
                            formData.value = part.substring(part.indexOf('\r\n\r\n') + 4, part.length - 1);
                            formData.type = 'Text';
                        }
                    }
                }
            } else {
                continue;
            }
            result.push(formData);
        }
        return result;
    };
    /**
     * 将源对象的字段复制到目标对象集合中的每一个对象中
     *
     * @param targets   目标对象集合
     * @param source    源对象
     * @returns {[]}    合并后的对象集合
     */
    Utils.unionListFrom = (targets, source) => {
        let result = [];
        targets.forEach(target => {
            result.push(Utils.unionFrom(target, source))
        });
        return result;
    };
    /**
     * 合并对象属性
     *
     * @param target    目标对象
     * @param source    源对象
     * @returns {{}}    合并后的对象
     */
    Utils.unionFrom = (target, source) => {
        let result = {};
        Object.keys(target).forEach(key => {
            result[key] = target[key];
            Object.keys(source).forEach(sourceKey => {
                result[sourceKey] = source[sourceKey];
            })
        })
        return result;
    };
    /**
     * 根据指定的分隔符截断URL
     *
     * @param url   URL
     * @param delimiter 分隔符
     * @returns {any}   截断后的URL
     */
    Utils.truncateUrl = (url, delimiter) => {
        let index = url.indexOf(delimiter);
        return index !== -1 ? url.substring(0, index) : url;
    };
    Utils.isArray = function(o){
        return Object.prototype.toString.call(o) === '[object Array]';
    };
    Utils.toQueryString = function(params) {
        let queryString = '';
        !!params && Utils.isArray(params) && params.forEach(item => {
            queryString = queryString + item.name + '=' + item.value + '&';
        });
        queryString = queryString.substring(0, queryString.length - 1);
        return queryString;
    };
    /**
     * 从uri中获取queryString
     *
     * @param uri   request.uri
     * @returns [{}]    queryString
     */
    Utils.getQueryString = (uri) => {
        let queryList = [];
        if (uri.indexOf('?') !== -1) {
            let query = uri.split('?')[1];
            let search = new URLSearchParams(query);
            for (let item of search) {
                queryList.push({name: item[0], value: item[1]})
            }
        }
        return queryList;
    };
    /**
     * 解析请求头中的Cookie
     *
     * @param requestCookies 请求头中的Cookie
     * @returns [{}] cookie list
     */
    Utils.parseRequestCookie = (requestCookies) => {
        let cookies = [];
        for (let cookie of requestCookies) {
            let cookieFieldList = cookie.split(';');
            for (let cookieField of cookieFieldList) {
                cookies.push({
                    'name': cookieField.split('=')[0].trim(),
                    'value': !!cookieField.split('=')[1] && cookieField.split('=')[1].trim()
                })
            }
        }
        return cookies;
    };
    Utils.decodeHex = (hex) => {
        let content;
        try {
            content = CryptoJS.enc.Utf8.stringify(CryptoJS.enc.Hex.parse(hex));
        } catch (e) {
            content = CryptoJS.enc.Latin1.stringify(CryptoJS.enc.Hex.parse(hex));
        }
        return content;
    }
    /**
     * 解析响应头中的 Set-Cookie
     *
     * @param responseHeaders  响应头
     * @returns [{}]    cookie list
     */
    Utils.parseResponseCookie = (responseHeaders) => {
        let cookies = [];
        if (!!responseHeaders) {
            let responseCookies = responseHeaders.filter(header => header.name === 'Set-Cookie');
            if (responseCookies.length !== 0) {
                for (let i = 0; i < responseCookies.length; i++) {
                    let cookieText = responseCookies[i].value;
                    let cookieFieldList = cookieText.split(';');
                    let cookie = {};
                    for (let i = 0; i < cookieFieldList.length; i++) {
                        if (cookieFieldList[i].trim() === '') {
                            continue;
                        }
                        let field = cookieFieldList[i].split('=')[0].toLowerCase().trim();
                        let value = cookieFieldList[i].split('=')[1];
                        if (field === 'httponly') {
                            cookie['HTTPOnly'] = 'true';
                        } else if (field === 'secure') {
                            cookie['secure'] = 'true';
                        } else if (field === 'max-age') {
                            cookie['Max-Age'] = value;
                        } else if (field === 'expires') {
                            cookie['expires'] = Utils.dateFormat('YYYY-mm-dd HH:MM:SS', new Date(value))
                        } else if (field === 'path') {
                            cookie['path'] = value;
                        } else if (field === 'samesite') {
                            cookie['SameSite'] = value;
                        } else if (field === 'domain') {
                            cookie['domain'] = value;
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
    };
    /**
     * 日期格式化
     *
     * @param fmt   pattern
     * @param date  日期
     * @returns {*}
     */
    Utils.dateFormat = (fmt, date) => {
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
    };
    /**
     * 替换文本中的特殊字符，用于v-html 显示HTML 源码
     *
     * @param text 替换前的文本
     * @returns text 替换后的文本
     */
    Utils.replaceSpecialChar = (text) => {
        return text.replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#39;')
            .replace(/\n/g, '<br>');
    };
    /**
     * 将 CryptoJS.enc 解码后words 类型的数据转为byte array
     *
     * @param words CryptoJS.enc 解码后的words
     * @returns byte array
     */
    Utils.wordsToByteArray = (words) => {
        let byteArray = [], word, i, j;
        for (i = 0; i < words.length; ++i) {
            word = words[i];
            for (j = 3; j >= 0; --j) {
                byteArray.push((word >> 8 * j) & 0xFF);
            }
        }
        return byteArray;
    };
    /**
     * 将byte array 转为字符串
     *
     * @param byteArray
     * @returns {string}
     */
    Utils.byteArrayToString = (byteArray) => {
        let binary = '';
        let bytes = new Uint8Array(byteArray);
        let len = bytes.byteLength;
        for (let i = 0; i < len; i++) {
            binary += String.fromCharCode(bytes[i]);
        }
        return binary;
    };
    /**
     * 将 CryptoJS.enc 解码后words 类型的数据转为字符串
     *
     * @param words CryptoJS.enc 解码后的words
     * @returns string
     */
    Utils.wordsToString = (words) => {
        return Utils.byteArrayToString(Utils.wordsToByteArray(words));
    };
    Utils.getScrollBarWidth = () => {
        let el = document.createElement("p"),
            styles = {
                width: "100px",
                height: "100px",
                overflowY: "scroll"
            },
            i;

        // 这里很巧妙呀，先定义了一个styles对象，里面写了各种样式值，然后通过for in将这个对象的值赋给p元素的style对象
        // 而不用通过style.width=***等来给p的样式对象赋值。
        for (i in styles) {
            el.style[i] = styles[i];
        }

        // 将元素加到body里面
        document.body.appendChild(el);

        let scrollBarWidth = el.offsetWidth - el.clientWidth;
        //将添加的元素删除
        el.remove();
        return scrollBarWidth;
    };
    win.Utils = Utils;
    /**
     * JSON 格式化
     *
     * @param text  未格式的JSON
     * @returns {string}    格式化后的JSON
     */
    win.JSON.pretty = (text) => {
        text = text.replace(/ufffd/g, '\\----')
        let json = JSON.stringify(JSON.parse(text), null, 2);
        return json.replace(/\\----/g, 'ufffd');
    }
})(window);