const input = document.querySelector(".input-area input");
const sendButton = document.querySelector(".input-area button");
const chatBox = document.querySelector(".chat-box");

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

    try {

        const response = await fetch("/chat", {
            method: "POST",
            body: message
        });

        const answer = await response.text();

        // Bot message
        const botMessage = document.createElement("div");
        botMessage.className = "bot-message";
        botMessage.textContent = answer;

        chatBox.appendChild(botMessage);

    } catch (error) {

        const botMessage = document.createElement("div");
        botMessage.className = "bot-message";
        botMessage.textContent = "Server connection error.";

        chatBox.appendChild(botMessage);
    }

    chatBox.scrollTop = chatBox.scrollHeight;
}

sendButton.addEventListener("click", sendMessage);

input.addEventListener("keydown", function(event) {

    if (event.key === "Enter") {
        sendMessage();
    }

});