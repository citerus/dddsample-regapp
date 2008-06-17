package se.citerus.dddsample.web.command;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validator for {@link se.citerus.dddsample.web.command.TrackCommand}s.
 */
public final class TrackCommandValidator implements Validator {

  public boolean supports(final Class clazz) {
    return TrackCommand.class.isAssignableFrom(clazz);
  }

  public void validate(final Object object, final Errors errors) {
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "trackingId", "error.required", "Required");
  }

}

