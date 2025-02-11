package org.joget.marketplace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.service.AppPluginUtil;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.datalist.model.DataList;
import org.joget.apps.datalist.model.DataListColumn;
import org.joget.apps.datalist.model.DataListColumnFormatDefault;
import org.joget.commons.util.LogUtil;

public class StatusColorDatalistFormatter extends DataListColumnFormatDefault {

    private final static String MESSAGE_PATH = "messages/StatusColorDatalistFormatter";

    public String getName() {
        return "Status Color Datalist Formatter";
    }

    public String getVersion() {
        return "7.0.0";
    }

    public String getDescription() {
        //support i18n
        //=======================================================================================================
        return AppPluginUtil.getMessage("org.joget.marketplace.StatusColorDatalistFormatter.pluginDesc", getClassName(), MESSAGE_PATH);
    }

    @Override
    public String format(DataList dataList, DataListColumn column, Object row, Object value) {
        AppDefinition appDef = AppUtil.getCurrentAppDefinition();
        String result = (String) value;

        if (result != null && !result.isEmpty()) {
            try {
                // Process hash variables
                boolean isCaseSensitive = Boolean.parseBoolean((getPropertyString("statusCaseSensitivity")));

                // Validate options property
                Object optionsProperty = getProperty("options");
                if (!(optionsProperty instanceof Object[])) {
                    return result; // Return early if options are invalid
                }
                Object[] options = (Object[]) optionsProperty;

                // Store formatted options
                ArrayList<Map<String, String>> optionMap = new ArrayList<>();

                for (Object o : options) {
                    Map<String, String> mapping = (Map<String, String>) o;
                    String mappingValue = mapping.get("value");

                    if ((isCaseSensitive && value.equals(mappingValue)) || (!isCaseSensitive && result.equalsIgnoreCase(mappingValue))) {
                        result = generateStyledHtml(mapping);
                        break; // Exit loop early after a match
                    }

                    optionMap.add(mapping);
                }

                // Store model data
                Map<String, Object> model = new HashMap<>();
                model.put("options", optionMap);
                model.put("columnName", column.getName());
                model.put("element", this);

            } catch (Exception e) {
                LogUtil.error(getClass().getName(), e, "Error occurred while formatting DataList column: " + column.getName());
            }
        }
        return result;
    }

    // Centralized function for generating formatted HTML
    private String generateStyledHtml(Map<String, String> mapping) {
        return String.format(
                "<p style=\"color: white; background-color: %s; white-space: nowrap; " +
                        "border-radius: 8px; padding:6px; text-align: center; margin: 0px;\">%s</p>",
                mapping.get("backgroundColor"), mapping.get("label")
        );
    }


    public String getLabel() {
        //support i18n
        return AppPluginUtil.getMessage("org.joget.marketplace.StatusColorDatalistFormatter.pluginLabel", getClassName(), MESSAGE_PATH);
    }

    public String getClassName() {
        return getClass().getName();
    }

    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClassName(), "/properties/StatusColorDatalistFormatter.json", null, true, MESSAGE_PATH);
    }
}
