
const emailWrong = window.emailWrong
const invitationSent = window.invitationSent

function validateEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

function invite() {
    const role = document.getElementById("role")
    const email = document.getElementById("email")
    const emailMessage = document.getElementById("emailMessage")
    if (validateEmail(email.value)){
        emailMessage.innerText=invitationSent+' '+email.value
        emailMessage.style.color="green"
        fetch('/invite?email='+email.value+'&role='+role.value)
    }
    else {
        emailMessage.style.color="red"
        emailMessage.innerText=emailWrong
    }

    email.value=''
}