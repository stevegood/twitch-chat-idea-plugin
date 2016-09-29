package rocks.stevegood.idea.twitch

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import groovy.xml.MarkupBuilder
import org.jetbrains.annotations.NotNull
import tv.extrememoderation.twitch.chat.TwitchBot

/**
 * Created by sgood on 9/23/16.
 */
class TwitchChatToolWindowFactory implements ToolWindowFactory {
    private ToolWindow twitchTW
    private TwitchChatToolWindow chatWindow
    private List messages

    TwitchChatToolWindowFactory() {
        messages = []
        chatWindow = new TwitchChatToolWindow()
        chatWindow.settingsButton.addActionListener {
            println 'Settings button pressed!'
        }

        chatWindow.sendChatButton.addActionListener sendChatAction
    }

    @Override
    void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        this.twitchTW = toolWindow
        ContentFactory contentFactory = ContentFactory.SERVICE.instance
        Content content = contentFactory.createContent chatWindow.twitchChatContent, '', false
        twitchTW.contentManager.addContent content
        chatWindow.chatInput.requestFocusInWindow()
    }

    private Closure sendChatAction = {
        Map message = [
            date: new Date(),
            text: chatWindow.chatInput.text,
            author: 'ChesterTester'
        ]
        println "Sending chat content: ${message}"
        chatWindow.chatInput.text = ''
        messages << message
        updateChat()
    }

    private void updateChat() {
        StringWriter writer = new StringWriter()
        new MarkupBuilder(writer).html {
            head ''
            body {
                messages.each { message ->
                    p {
                        small message.date
                        strong " $message.author: "
                        span message.text
                    }
                }
            }
        }

        chatWindow.chatTextPane.text = writer.toString()
    }
}
