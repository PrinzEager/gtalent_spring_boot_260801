package student.eg.gtalent_spring_boot_260801.exception;

public class MemberAccountException extends ApiException {

    public MemberAccountException(String errorKey, String messageCode) {
        super(errorKey, messageCode);
    }


}

