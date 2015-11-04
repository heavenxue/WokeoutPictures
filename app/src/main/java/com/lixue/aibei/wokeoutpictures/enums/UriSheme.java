package com.lixue.aibei.wokeoutpictures.enums;

/**
 * 支持的协议类型
 * Created by Administrator on 2015/11/4.
 */
public enum UriSheme {
    HTTP("http://"){
        @Override
        public String createUri(String uri){
            return uri;
        }

        @Override
        public String crop(String uri) {
            return uri;
        }
    },
    HTTPS("https://"){
        @Override
        public String createUri(String uri){
            return uri;
        }

        @Override
        public String crop(String uri) {
            return uri;
        }
    },
    FILE("/"){
        @Override
        public String createUri(String uri){
            return uri;
        }

        @Override
        public String crop(String uri) {
            return uri;
        }
    },
    CONTENT("content://"){
        @Override
        public String createUri(String uri){
            return uri;
        }

        @Override
        public String crop(String uri) {
            return uri;
        }
    },
    ASSET("asset://"){
        @Override
        public String createUri(String content){
            if(content == null || "".equals(content.trim())){
                return null;
            }
            return getUriPrefix()+content;
        }

        @Override
        public String crop(String uri) {
            if (!uri.startsWith(getUriPrefix())) {
                throw new IllegalArgumentException(String.format("URI [%1$s] doesn't have expected scheme [%2$s]", uri, getUriPrefix()));
            }
            return uri.substring(getUriPrefix().length());
        }
    },

    DRAWABLE("drawable://"){
        @Override
        public String createUri(String content){
            if(content == null || "".equals(content.trim())){
                return null;
            }
            return getUriPrefix()+content;
        }

        @Override
        public String crop(String uri) {
            if (!uri.startsWith(getUriPrefix())) {
                throw new IllegalArgumentException(String.format("URI [%1$s] doesn't have expected scheme [%2$s]", uri, getUriPrefix()));
            }
            return uri.substring(getUriPrefix().length());
        }
    };

    private String uriPrefix;

    UriSheme(String uriPrefix) {
        this.uriPrefix = uriPrefix;
    }

    public abstract String createUri(String content);

    public abstract String crop(String uri);

    public String getUriPrefix() {
        return uriPrefix;
    }

    public static UriSheme valueOfUri(String uri) {
        if (uri != null && !"".equals(uri.trim())) {
            for (UriSheme uriScheme : values()) {
                if (uri.startsWith(uriScheme.getUriPrefix())) {
                    return uriScheme;
                }
            }
        }
        return null;
    }
}
