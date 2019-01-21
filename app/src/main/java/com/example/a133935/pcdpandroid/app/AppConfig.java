package com.example.a133935.pcdpandroid.app;

public class AppConfig {
    // Server user login url
    public static String URL_LOGIN = "http://10.0.2.2:8085/android_login_api/login.php";

    // Server user register url
    public static String URL_REGISTER = "http://10.0.2.2:8085/android_login_api/register.php";

    //Server create new poll
    public static String URL_CREATE_POLL = "http://10.0.2.2:8085/android_login_api/createpoll.php";

    //Server get polls by the user
    public static String URL_GETPOLLS = "http://10.0.2.2:8085/android_login_api/getpolls.php";

    //Server get poll details
    public static String URL_GETPOLLDETAILS = "http://10.0.2.2:8085/android_login_api/getpolldetails.php";

    //Server end user poll
    public static String URL_ENDPOLL = "http://10.0.2.2:8085/android_login_api/endpoll.php";

    //Server get poll results
    public static String URL_GETPOLLRESULTS = "http://10.0.2.2:8085/android_login_api/getpollresults.php";

    //Server check poll status active or not
    public static String URL_CHECKSTATUS = "http://10.0.2.2:8085/android_login_api/checkstatus.php";

    //Server cast user vote
    public static String URL_VOTE = "http://10.0.2.2:8085/android_login_api/vote.php";
}
