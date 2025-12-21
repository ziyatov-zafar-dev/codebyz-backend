package uz.codebyz.message.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import uz.codebyz.common.ResponseDto;
import uz.codebyz.message.service.MessageException;

@RestControllerAdvice
public class MessageExceptionHandler {

    @ExceptionHandler(MessageException.class)
    public ResponseEntity<ResponseDto<Void>> handle(MessageException ex) {
        return ResponseEntity.badRequest().body(ResponseDto.fail(400, null, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<Void>> handleUnknown(Exception ex) {
        return ResponseEntity.internalServerError().body(ResponseDto.fail(500, null, "Beklenmeyen hata: " + ex.getMessage()));
    }
}
