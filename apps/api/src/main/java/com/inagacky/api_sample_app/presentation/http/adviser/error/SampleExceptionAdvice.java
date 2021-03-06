package com.inagacky.api_sample_app.presentation.http.adviser.error;

import com.inagacky.api_sample_app.exception.BaseSampleException;
import com.inagacky.api_sample_app.presentation.http.response.error.ErrorDetail;
import com.inagacky.api_sample_app.presentation.http.response.error.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 *
 * Exceptionのハンドラ　
 *
 */
@ControllerAdvice
@Slf4j
public class SampleExceptionAdvice {

    @Autowired
    MessageSource messageSource;

    /**
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    private ErrorResponse handleNotValidException(MethodArgumentNotValidException exception) {

        log.info("NotValidException Error", exception);

        return createErrorResponse(exception);
    }

    /**
     * @param exception
     * @return
     */
    @ExceptionHandler(BaseSampleException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    private ErrorResponse handleSampleException(BaseSampleException exception) {

        log.warn("BaseSampleException Error", exception);

        return createErrorResponse(exception);
    }

    /**
     * @param exception
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    private ErrorResponse handleException(Exception exception) {

        log.error("Exception Error", exception);

        return createErrorResponse(exception);
    }

    /**
     * 例外パターンに対して、エラーコードの設定とメッセージの設定を行う。
     * @param exception
     * @return
     */
    private ErrorResponse createErrorResponse(Exception exception) {

        ErrorResponse errorResponse = new ErrorResponse();

        if (exception instanceof MethodArgumentNotValidException) {

            MethodArgumentNotValidException methodArgumentNotValidException = (MethodArgumentNotValidException)exception;
            BindingResult bindingResult = methodArgumentNotValidException.getBindingResult();
            setErrorCode(errorResponse, exception);
            errorResponse.setErrorDetails(createValidateErrorDetailList(bindingResult));
        } else if (exception instanceof BaseSampleException) {

            setErrorCode(errorResponse, exception);
            errorResponse.setErrorMessage(messageSource.getMessage("sample.error.message", null, Locale.getDefault()));
        } else {

            // 汎用エラー
            setErrorCode(errorResponse, exception);
            errorResponse.setErrorMessage(messageSource.getMessage("sample.error.message", null, Locale.getDefault()));
        }

        return errorResponse;
    }

    /**
     * エラーフィールドに対しての詳細設定
     *
     * @param bindingResult
     * @return
     */
    private List<ErrorDetail> createValidateErrorDetailList(BindingResult bindingResult) {

        List<ErrorDetail> errorDetailList = new ArrayList<>();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {

            ErrorDetail errorDetail = new ErrorDetail();

            String errorType = fieldError.getCode();
            setValidateErrorDetailCode(errorDetail, errorType);
            errorDetail.setErrorField(fieldError.getField());

            String fieldName = messageSource.getMessage(fieldError.getField(), null, null, Locale.getDefault());
            if (fieldName == null) {
                errorDetail.setErrorMessage(fieldError.getDefaultMessage());
            } else {
                errorDetail.setErrorMessage(messageSource.getMessage(errorType, new String[]{fieldName}, Locale.getDefault()));
            }

            log.info("Error Field:[{}] Error Message:[{}]", errorDetail.getErrorField(), errorDetail.getErrorMessage());
            errorDetailList.add(errorDetail);
        }

        return errorDetailList;
    }

    /**
     * エラーコードの設定
     * @param errorResponse
     * @param exception
     *
     */
    private void setErrorCode(ErrorResponse errorResponse, Exception exception) {

        errorResponse.setErrorCode(ErrorResponse.ErrorCode.UNKNOWN_ERROR);

        if (exception instanceof MethodArgumentNotValidException) {
            // TODO: Please Set Error Code
        } else {
            errorResponse.setErrorCode(ErrorResponse.ErrorCode.UNKNOWN_ERROR);
        }
    }

    /**
     * エラー詳細コードの設定　
     *
     * @param errorDetail
     * @param errorType
     */
    private void setValidateErrorDetailCode(ErrorDetail errorDetail, String errorType) {

        switch (errorType) {
            case "Error1":
                errorDetail.setErrorDetailCode(ErrorDetail.ErrorDetailCode.ERROR_1);
                break;
            case "Error2":
                errorDetail.setErrorDetailCode(ErrorDetail.ErrorDetailCode.ERROR_2);
                break;
            default:
                break;
        }
    }
}
