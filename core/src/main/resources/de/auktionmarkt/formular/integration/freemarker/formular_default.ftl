<#macro start_form form_specification form_state action_extra>
    <form action="${form_specification.getAction(action_extra)}" method="${form_specification.method}">
</#macro>

<#macro end_form form_specification form_state>
    </form>
</#macro>

<#macro render_field form_specification field_specification form_state field_state>
    <#if (field_specification.parameters.titleSupplier)??><h3>${field_specification.parameters.titleSupplier.get()}</h3></#if>
    <div>
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
    </div>
</#macro>

<#-- Renders an input field. Input type will be the same as field_specification.type. -->
<#macro render_input_field form_specification field_specification form_state field_state>
    <label for="${field_specification.path}">${field_specification.label}</label>
    <@compress single_line=true><input type="${field_specification.type}"
        id="${field_specification.path}"
        name="${field_specification.path}"
        <#if field_state.value?has_content>value="${field_state.value}"</#if>
        <#if field_state.errors?has_content>class="invalid"<#elseif form_state.submitted>class="valid"</#if>
        <#if (field_specification.parameters.required)?? && field_specification.parameters.required == true>required</#if>
        <#if field_specification.parameters.minValue??>min="${field_specification.parameters.minValue?c}"</#if>
        <#if field_specification.parameters.maxValue??>max="${field_specification.parameters.maxValue?c}"</#if>></@compress>
    <#if field_state.errors?has_content>
        <span class="form-errors">Errors on this field: <#list field_state.errors as error>${error}<#sep>, </#list></span>
    </#if>
</#macro>

<#macro render_checkbox form_specification field_specification form_state field_state>
    <#if field_specification.valuesSupplier??>
        <h4>${field_specification.label}</h4>
        <#list field_specification.valuesSupplier.get() as value, label>
            <label for="${field_specification.path}-${value?index}">
                <@compress single_line=true><input type="checkbox"
                    id="${field_specification.path}-${value?index}"
                    name="${field_specification.path}"
                    value="${value}"
                    <#if field_state.valueContains(value)>checked</#if>
                    <#if (field_specification.parameters.required)?? && field_specification.parameters.required == true>required</#if>>
                ${label}</@compress>
            </label>
        </#list>
    <#else>
        <label for="${field_specification.path}">
            <@compress single_line=true><input type="checkbox"
                id="${field_specification.path}"
                name="${field_specification.path}"
                <#if field_state.isChecked()>checked</#if>
                <#if (field_specification.parameters.required)?? && field_specification.parameters.required == true>required</#if>>
            ${field_specification.label}</@compress>
        </label>
    </#if>
    <#if field_state.errors?has_content>
        <span class="form-errors">Errors on this field: <#list field_state.errors as error>${error}<#sep>, </#list></span>
    </#if>
</#macro>

<#macro render_selector form_specification field_specification form_state field_state>
    <label for="${field_specification.path}">${field_specification.label}</label>
    <select id="${field_specification.path}" name="${field_specification.path}">
        <#list field_specification.valuesSupplier.get() as value, label>
            <option value="${value}" <#if field_state.valueContains(value)>selected</#if>>${label}</option>
        </#list>
    </select>
    <#if field_state.errors?has_content>
        <span class="form-errors">Errors on this field: <#list field_state.errors as error>${error}<#sep>, </#list></span>
    </#if>
</#macro>

<#macro render_button form_specification field_specification form_state field_state>
    <button <#if field_specification.type != 'button'>type="${field_specification.type}"</#if>>${field_specification.label}</button>
</#macro>
