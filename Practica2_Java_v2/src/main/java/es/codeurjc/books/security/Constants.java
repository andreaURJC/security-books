package es.codeurjc.books.security;

public class Constants {
    public static final String LOGIN_URL = "/login";
    public static final String ALL_BOOKS_URL = "/api/v1/books/";
    public static final String AUTHORITIES_KEY = "CLAIM_TOKEN";
    public static final String SIGNING_KEY = "KEY_1234";
    public static final int ACCESS_TOKEN_VALIDITY_SECONDS = 28800;
    public static final String ISSUER_TOKEN = "ISSUER";
    public static final String HEADER_AUTHORIZATION_KEY = "Authorization";
    public static final String TOKEN_BEARER_PREFIX = "Bearer";

}