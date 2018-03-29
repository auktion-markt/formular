<#import "formular_default.ftl" as default_theme/>

<#macro render_form form_specification form_state action_extra={} form_theme=default_theme>
    <@form_theme.start_form form_specification form_state action_extra/>
    <#list form_specification.fields as field_id, field_specification>
        <@form_theme.render_field form_specification field_specification form_state form_state.getFieldState(field_id)/>
    </#list>
    <@form_theme.end_form form_specification form_state/>
</#macro>
