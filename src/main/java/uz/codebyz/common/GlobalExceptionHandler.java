package uz.codebyz.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseDto<Void> validation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().isEmpty()
                ? "Validation error"
                : ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();

        return ResponseDto.fail(
                400,
                ErrorCode.VALIDATION_ERROR,
                msg
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseDto<Void> handle(Exception ex) {
//        ex.printStackTrace(); // 🔥 REAL ERROR CONSOLE’DA KO‘RINADI
        log.error(ex.getMessage());
        return ResponseDto.fail(
                500,
                ErrorCode.INTERNAL_ERROR,
                ex.getClass().getSimpleName() + ": " + ex.getMessage()
        );
    }
}
