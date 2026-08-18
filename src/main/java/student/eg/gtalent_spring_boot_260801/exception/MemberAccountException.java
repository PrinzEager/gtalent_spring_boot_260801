package student.eg.gtalent_spring_boot_260801.exception;

public class MemberAccountException extends RuntimeException {
    private final String errorKey;
    private final String messageCode;

    public MemberAccountException(String errorKey, String messageCode) {
        super(messageCode);
        this.errorKey = errorKey;
        this.messageCode = messageCode;
    }

    public String getErrorKey() {
        return errorKey;
    }

    public String getMessageCode() {
        return messageCode;
    }   

}

