(function (win) {
    let Utils = {};
    Utils.parseFormData = (contentType, requestContent) => {

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
    Utils.truncate = (url, delimiter) => {
        let index = url.indexOf(delimiter);
        return index !== -1 ? url.substring(0, index) : url;
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
            let arr = query.split('&');
            for (let i = 0; i < arr.length; i++) {
                queryList.push({name: arr[i].split('=')[0], value: arr[i].split('=')[1]});
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
                    'value': cookieField.split('=')[1].trim()
                })
            }
        }
        return cookies;
    };
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