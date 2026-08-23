# Springboot_EmailService_Automated
## Springboot_EmailService_Automated_sends email as per cron , when started.

### Note:
Try avoiding new keyword in spring boot , and try to autowire it , see that email service eg while you autowired that emailsender ( a dependency in email service class ) interface but but you created tha  the object with new keyword so it will not inject that emailsender object, and will throw error saying it's null , so try to autowire the email service class and call by this . It's ref and call it's method and it wont throw an error. 
## Summary 
Try to autowire the services as it's dependency are also autowired and try avoiding new keyword for the services at most and others classes too , try to use autowired in spring boot
