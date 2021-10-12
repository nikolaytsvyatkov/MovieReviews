package com.fmi.reviews.web.mvc;

import com.fmi.reviews.exception.InvalidEntityDataException;
import com.fmi.reviews.exception.UnautorizedRequestException;
import com.fmi.reviews.exception.UnexistingEntityException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;

import java.nio.file.FileSystemException;

@ControllerAdvice(basePackageClasses = ErrorHandlerMvcControllerAdvice.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ErrorHandlerMvcControllerAdvice {
    @ExceptionHandler({MaxUploadSizeExceededException.class, FileSystemException.class})
    @Order(1)
    public ModelAndView handleUploadExceptions(Exception ex) {
        ModelAndView modelAndView = new ModelAndView("errors");
        modelAndView.getModel().put("message", ex.getMessage());
        return modelAndView;
    }

    @ExceptionHandler({UnautorizedRequestException.class, UnexistingEntityException.class, InvalidEntityDataException.class})
    @Order(2)
    public ModelAndView handle(Exception ex) {
        ModelAndView modelAndView = new ModelAndView("errors");
        modelAndView.addObject("message", ex.getMessage());
        modelAndView.addObject("continueUrl", "/movies");
        return modelAndView;
    }
}
