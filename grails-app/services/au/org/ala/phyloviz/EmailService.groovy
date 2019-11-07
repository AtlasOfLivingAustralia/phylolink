package au.org.ala.phyloviz

import org.springframework.validation.Errors

class EmailService {
    def grailsApplication

    void sendDoiFailureEmail(Object recipient, String sender, String doi, Errors errors) {
        sendEmailView(recipient, sender, "Failure Alert - DOI ${doi}", [doi: doi, error: errors], '/emails/doi-failure')
    }

    void sendEmailView(Object recipient, String sender, String subjectText, Map model, String htmlView, String textView = null) {
        log.debug("Sending email to ${recipient} with subject '${subjectText}'")

        if (recipient) {
            sendMail {
                to recipient
                from sender
                subject subjectText
                html(view: htmlView, model: model)
                if (textView) {
                    text(view: textView)
                }
            }
        }
    }
}
