package com.blog.exception;

public class CacheException extends RuntimeException {
    private static final long serialVersionUID = -2307847433631929663L;

    private String msg;

    public CacheException(Exception ex , String msg) { this.msg = msg; }

    public String getMsg()
    {
        return this.msg;
    }
}
