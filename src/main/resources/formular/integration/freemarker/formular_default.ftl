<#macro start_form form_specification form_state action_extra>
    <form action="${form_specification.getAction(action_extra)}" method="${form_specification.method}">
</#macro>

<#macro end_form form_specification form_state>
    </form>
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
            <#break>
        <#case 'radio'>
            <#break>
        <#case 'select'>
            <#break>
    </#switch>
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
