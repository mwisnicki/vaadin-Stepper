package org.vaadin.risto.stepper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.vaadin.risto.stepper.widgetset.client.shared.DateStepperField;
import org.vaadin.risto.stepper.widgetset.client.shared.DateStepperState;

/**
 * <p>
 * Field that allows stepping through values via given up/down controls.
 * Supports values of type Date. The default value is today.
 * </p>
 * 
 * @author Risto Yrjänä / Vaadin }>
 * 
 */
public class DateStepper extends AbstractStepper<Date, Integer> {

    private static final long serialVersionUID = 5238300195216371890L;
    private SimpleDateFormat dateFormat;

    public DateStepper() {
        setStepAmount(1);
        setStepField(DateStepperField.DAY);
        setValue(new Date());
    }

    public DateStepper(String caption) {
        this();
        setCaption(caption);
    }

    @Override
    public Class<Date> getType() {
        return Date.class;
    }

    @Override
    protected DateStepperState getState() {
        return (DateStepperState) super.getState();
    }

    @Override
    public void setLocale(Locale locale) {
        super.setLocale(locale);
    }

    /**
     * Set the field that the stepper should step through. The field must be one
     * of the ones defined by
     * {@link org.vaadin.risto.stepper.widgetset.client.shared.DateStepperField
     * )}
     * 
     * @param field
     * @see org.vaadin.risto.stepper.widgetset.client.sharedDateStepperField
     */
    public void setStepField(DateStepperField field) {
        getState().setDateStep(field);
    }

    @Override
    public void setMaxValue(Date maxValue) {
        super.setMaxValue(normalizeBoundaryDate(maxValue));
    }

    @Override
    public void setMinValue(Date minValue) {
        super.setMinValue(normalizeBoundaryDate(minValue));
    }

    @Override
    public void beforeClientResponse(boolean initial) {
        super.beforeClientResponse(initial);

        getState().setDateFormat(dateFormatToPattern(getDateFormat()));
    }

    @Override
    protected boolean isValidForRange(Date value) {
        if (value == null) {
            return true;
        }

        Date dateValue = value;
        Calendar valueCalendar = Calendar.getInstance(getLocale());
        valueCalendar.setTime(dateValue);
        Calendar compareCalendar = (Calendar) valueCalendar.clone();

        if (getMaxValue() != null) {
            compareCalendar.setTime(getMaxValue());
            if (valueCalendar.after(compareCalendar)) {
                return false;
            }
        }

        if (getMinValue() != null) {
            compareCalendar.setTime(getMinValue());
            if (valueCalendar.before(compareCalendar)) {
                return false;
            }
        }

        return true;
    }

    @Override
    protected Date parseStringValue(String value)
            throws StepperValueParseException {
        if (value == null || "".equals(value)) {
            return null;
        }

        DateFormat df = getDateFormat();
        try {
            return df.parse(value);
        } catch (ParseException e) {
            throw new StepperValueParseException(e);
        }
    }

    /**
     * Normalizes max and min dates by resetting hours, minutes etc.
     * 
     * @param boundaryDate
     * @return
     */
    protected Date normalizeBoundaryDate(Date boundaryDate) {
        if (boundaryDate == null) {
            return null;
        }

        Calendar javaCalendar = Calendar.getInstance(getLocale());
        javaCalendar.setTime(boundaryDate);

        javaCalendar.set(Calendar.MILLISECOND, 0);
        javaCalendar.set(Calendar.SECOND, 0);
        javaCalendar.set(Calendar.MINUTE, 0);
        javaCalendar.set(Calendar.HOUR, 0);
        javaCalendar.set(Calendar.HOUR_OF_DAY, 0);

        return javaCalendar.getTime();
    }

    @Override
    protected String parseValueToString(Date value) {
        if (value == null) {
            return super.parseValueToString(value);
        }
        DateFormat df = getDateFormat();

        return df.format(value);
    }

    public DateFormat getDateFormat() {
        if (dateFormat != null) {
            return dateFormat;
        } else {
            return DateFormat.getDateInstance(DateFormat.SHORT, getLocale());
        }
    }

    /**
     * Set the {@link SimpleDateFormat} used to format the value of this field.
     * If set to null (as it is by default), DateStepper will generate a short
     * date format based on the current locale.
     * 
     * Please note that this feature is experimental, and not all patterns are
     * supported by the client-side implementation.
     * 
     * @param dateFormat
     */
    public void setDateFormat(SimpleDateFormat dateFormat) {
        this.dateFormat = dateFormat;
        markAsDirty();
    }

    protected static String dateFormatToPattern(DateFormat dateFormat) {
        if (dateFormat instanceof SimpleDateFormat) {
            return ((SimpleDateFormat) dateFormat).toPattern();
        } else {
            throw new IllegalArgumentException(
                    "Unable to form date pattern from " + dateFormat);
        }
    }
}