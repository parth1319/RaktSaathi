package com.parth.raktsaathi;

public class Urls {

    public static final String BASE_URL = "http://10.189.196.98:80/RaktsaathiAPI/";

    // Registration and Login & Google Sign-in PHP
    public static final String REGISTER = BASE_URL + "user_registration.php";
    public static final String LOGIN = BASE_URL + "users_login.php";
    public static final String GoogleSignIn = BASE_URL + "google_login.php";
    public static final String GET_STATS = BASE_URL + "get_stats.php";
    public static final String GET_CAMPS = BASE_URL + "get_camps.php";
    public static final String ADD_CAMP = BASE_URL + "add_camp.php";
    public static final String UPDATE_CAMP = BASE_URL + "update_camp.php";
    public static final String CHECK_REGISTER = BASE_URL + "check_registered.php";
    public static final String REGISTER_CAMP = BASE_URL + "register_camp.php";


    //Forget Password PHP
    public static final String SEND_OTP = BASE_URL + "send_otp.php";
    public static final String VERIFY_OTP = BASE_URL + "verify_otp.php";
    public static final String RESET_PASSWORD = BASE_URL + "reset_password.php";

    //Profile and Image Upload PHP
    public static final String GET_PROFILE = BASE_URL + "get_profile.php";
    public static final String UPDATE_PROFILE = BASE_URL + "update_profile.php";
    public static final String UPLOAD_IMAGE = BASE_URL + "upload_image.php";
    public static final String CHANGE_PASSWORD = BASE_URL + "change_password.php";

    // Blood Donate and Donors PHP (Consolidated)
    public static final String DONATE_BLOOD = BASE_URL + "donate_blood.php";
    public static final String GET_DONORS = BASE_URL + "get_donors.php";
    public static final String SEARCH_DONORS = BASE_URL + "get_donors.php";

    //Blood Requests PHP
    public static final String REQUEST_BLOOD = BASE_URL + "request_blood.php";
    public static final String GET_REQUESTS = BASE_URL + "get_request.php";

}
