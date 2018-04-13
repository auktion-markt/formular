<#import "formular_default.ftl" as fallback/>

<#macro start_form form_specification form_state action_extra>
    <@fallback.macro start_form form_specification form_state action_extra />
</#macro>

<#macro end_form form_specification form_state>
    <@fallback.end_form form_specification form_state />
</#macro>

<#macro render_field form_specification field_specification form_state field_state>
    <#if (field_specification.parameters.titleSupplier)??><h3>${field_specification.parameters.titleSupplier.get()}</h3></#if>
    <#switch field_specification.type>
        <#case 'text'>
        <#case 'password'>
        <#case 'number'>
        <#case 'email'>
        <#case 'tel'>
        <#case 'url'>
        <#case 'date'>
        <#case 'datetime-local'>
        <#case 'time'>
            <@render_input_field form_specification field_specification form_state field_state/>
            <#break>
        <#case 'checkbox'>
            <@render_checkbox form_specification field_specification form_state field_state/>
            <#break>
        <#case 'radio'>
            <#break>
        <#case 'select'>
            <@render_selector form_specification field_specification form_state field_state/>
            <#break>
        <#case 'button'>
        <#case 'submit'>
        <#case 'cancel'>
            <@render_button form_specification field_specification form_state field_state/>
            <#break>
    </#switch>
</#macro>

<#-- Renders an input field. Input type will be the same as field_specification.type. -->
<#macro render_input_field form_specification field_specification form_state field_state>
    <div class="form-group">
        <label for="${field_specification.path}">${field_specification.label}</label>
        <@compress single_line=true><input type="${field_specification.type}"
            id="${field_specification.path}"
            name="${field_specification.path}"
            <#if field_state.value?has_content>value="${field_state.value}"</#if>
            class="form-control <#if field_state.errors?has_content>is-invalid</#if>"
            <#if (field_specification.parameters.required)?? && field_specification.parameters.required == true>required</#if>
            <#if field_specification.parameters.minValue??>min="${field_specification.parameters.minValue?c}"</#if>
            <#if field_specification.parameters.maxValue??>max="${field_specification.parameters.maxValue?c}"</#if>></@compress>
        <#if field_state.errors?has_content>
            <div class="invalid-feedback"><#list field_state.errors as error>${error}<#sep>, </#list></div>
        </#if>
    </div>
</#macro>

<#macro render_checkbox form_specification field_specification form_state field_state>
    <div class="form-group">
        <#if field_specification.valuesSupplier??>
            <h4>${field_specification.label}</h4>
            <#list field_specification.valuesSupplier.get() as value, label>
                <@compress single_line=true><input type="checkbox"
                    id="${field_specification.path}"
                    name="${field_specification.path}"
                    value="${value}"
                    <#if field_state.valueContains(value)>checked</#if>
                    <#if (field_specification.parameters.required)?? && field_specification.parameters.required == true>required</#if>></@compress>
                <label for="${field_specification.path}">${label}</label>
            </#list>
        <#else>
            <@compress single_line=true><input type="checkbox"
                id="${field_specification.path}"
                name="${field_specification.path}"
                <#if field_state.isChecked()>checked</#if>
                <#if (field_specification.parameters.required)?? && field_specification.parameters.required == true>required</#if>></@compress>
            <label for="${field_specification.path}">${field_specification.label}</label>
        </#if>
        <#if field_state.errors?has_content>
            <div class="invalid-feedback"><#list field_state.errors as error>${error}<#sep>, </#list></div>
        </#if>
    </div>
</#macro>

<#macro render_selector form_specification field_specification form_state field_state>
    <div class="form-group">
        <label for="${field_specification.path}">${field_specification.label}</label>
        <select id="${field_specification.path}" name="${field_specification.path}" class="form-control">
            <#list field_specification.valuesSupplier.get() as value, label>
                <option value="${value}" <#if field_state.valueContains(value)>selected</#if>>${label}</option>
            </#list>
        </select>
        <#if field_state.errors?has_content>
            <div class="invalid-feedback"><#list field_state.errors as error>${error}<#sep>, </#list></div>
        </#if>
    </div>
</#macro>

<#macro render_button form_specification field_specification form_state field_state>
    <button <#if field_specification.type != 'button'>type="${field_specification.type}"</#if> class="btn">${field_specification.label}</button>
</#macro>
