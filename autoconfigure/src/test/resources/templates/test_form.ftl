<#import "/formular.ftl" as formular />
<!DOCTYPE html>
<html>
    <head>
        <title>Test Form</title>
    </head>
    <body>
        <p>Mode: ${available?then('edit', 'create')}</p>
        <@formular.render_form form_specification form_state/>
    </body>
</html>
