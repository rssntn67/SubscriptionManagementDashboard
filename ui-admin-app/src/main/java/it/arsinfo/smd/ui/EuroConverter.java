package it.arsinfo.smd.ui;

import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.ui.renderers.NumberRenderer;
import it.arsinfo.smd.entity.SmdEntity;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class EuroConverter extends StringToBigDecimalConverter {
	
	public static NumberRenderer getEuroRenderer() {
		return new NumberRenderer(NumberFormat.getCurrencyInstance(SmdEntity.getLocalFromISO("EUR")));
	}

    /**
	 * 
	 */
	private static final long serialVersionUID = -566210843473994470L;

	public EuroConverter() {
        super("defaultErrorMessage");
    }

    public EuroConverter(String errorMessage) {
        super(errorMessage);
    }

    @Override
    public Result<BigDecimal> convertToModel(String value,
            ValueContext context) {
        if (value.isEmpty()) {
            return Result.ok(null);
        }
        value = value.replaceAll("[€\\s]", "").trim();
        if (value.isEmpty()) {
            value = "0";
        }
        return super.convertToModel(value, context);
    }

    @Override
    public String convertToPresentation(BigDecimal value,
            ValueContext context) {
        if (value == null) {
            return convertToPresentation(BigDecimal.ZERO, context);
        }
        return super.convertToPresentation(value, context) + " €";
    }

    @Override
    protected NumberFormat getFormat(Locale locale) {
        // Always display currency with two decimals
        NumberFormat format = super.getFormat(Locale.ITALIAN);
        if (format instanceof DecimalFormat) {
            format.setMaximumFractionDigits(2);
            format.setMinimumFractionDigits(2);
        }
        return format;
    }
}