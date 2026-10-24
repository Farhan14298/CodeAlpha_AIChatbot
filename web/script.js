const input = document.querySelector(".input-area input");
const sendButton = document.querySelector(".input-area button");
const chatBox = document.querySelector(".chat-box");

// AI response se unwanted Markdown symbols remove karega
function cleanResponse(text) {
    return text
        .replace(/\*\*/g, "")        // **bold**
        .replace(/__/g, "")          // __bold__
        .replace(/`{1,3}/g, "")      // `code` or ```code```
        .replace(/^#{1,6}\s*/gm, "") // # headings
        .replace(/^>\s?/gm, "")      // > quote
        .replace(/^\s*[-*+]\s+/gm, "• ") // list symbols ko bullet me badlega
        .trim();
}

async function sendMessage() {

    const message = input.value.trim();

    if (message === "") {
        return;
    }

    // User message
    const userMessage = document.createElement("div");
    userMessage.className = "user-message";
    userMessage.textContent = message;

    chatBox.appendChild(userMessage);

    input.value = "";

    // Send button temporarily disable
    sendButton.disabled = true;
    sendButton.textContent = "Thinking...";

    try {

        const response = await fetch("/chat", {
            method: "POST",
            body: message
        });

        const answer = await response.text();

        // Unwanted symbols remove
        const cleanAnswer = cleanResponse(answer);

        // Bot message
        const botMessage = document.createElement("div");
        botMessage.className = "bot-message";
        botMessage.textContent = cleanAnswer;

        chatBox.appendChild(botMessage);

    } catch (error) {

        const botMessage = document.createElement("div");
        botMessage.className = "bot-message";
        botMessage.textContent = "Server connection error.";

        chatBox.appendChild(botMessage);

    } finally {

        sendButton.disabled = false;
        sendButton.textContent = "Send";
        input.focus();
    }

    chatBox.scrollTop = chatBox.scrollHeight;
}

sendButton.addEventListener("click", sendMessage);

input.addEventListener("keydown", function(event) {

    if (event.key === "Enter") {
        sendMessage();
    }

});