package com.dinsaren.springbootjwtapi.constants;

public final class ErrorCode {

    public static final String SUCCESS = "SUC-000";
    public static final String GENERAL_ERROR = "ERR-000";

    public static final String BAD_REQUEST = "ERR-001";
    public static final String INTERNAL_SERVER_ERROR = "ERR-002";
    public static final String NAME_ALREADY_USE = "ERR-003";
    public static final String USER_NOT_PERMISSION = "ERR-004";

    // Rental domain error codes
    public static final String NOT_FOUND = "ERR-404";
    public static final String FORBIDDEN = "ERR-403";
    public static final String UNAUTHORIZED = "ERR-401";
    public static final String VALIDATION_ERROR = "ERR-422";
    public static final String DUPLICATE_FAVORITE = "ERR-010";
    public static final String INVALID_REVIEW = "ERR-011";
    public static final String INVALID_VISIT_REQUEST = "ERR-012";
    public static final String NOT_PROPERTY_OWNER = "ERR-013";
    public static final String INVALID_STATUS_TRANSITION = "ERR-014";
    public static final String DUPLICATE_ROOM_NUMBER = "ERR-015";

}
