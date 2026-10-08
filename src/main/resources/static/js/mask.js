function configurarToggleSenha(buttonId, inputId) {

    const button = document.getElementById(buttonId);
    const input = document.getElementById(inputId);

    if (!button || !input) return;

    button.addEventListener('click', function () {

        const tipoAtual = input.getAttribute('type');

        if (tipoAtual === 'password') {

            input.setAttribute('type', 'text');
            this.innerHTML = '<i class="bi bi-eye-slash"></i>';

        } else {

            input.setAttribute('type', 'password');
            this.innerHTML = '<i class="bi bi-eye"></i>';

        }

    });

}

configurarToggleSenha('togglePassword1', 'senha');
configurarToggleSenha('togglePassword2', 'confirmar_senha');




const telefoneInput = document.getElementById('telefone');

function aplicarMascaraTelefone(input) {

    let valor = input.value.replace(/\D/g, '');

    valor = valor.slice(0, 11);

    valor = valor.replace(/^(\d{2})(\d)/g, '($1) $2');
    valor = valor.replace(/(\d{5})(\d)/, '$1-$2');

    input.value = valor;

}

if (telefoneInput) {

    aplicarMascaraTelefone(telefoneInput);

    telefoneInput.addEventListener('input', function () {

        aplicarMascaraTelefone(this);

    });

}




const cpfInput = document.getElementById('cpf');

function aplicarMascaraCpf(input) {

    let valor = input.value.replace(/\D/g, '');

    valor = valor.slice(0, 11);

    valor = valor.replace(/(\d{3})(\d)/, '$1.$2');
    valor = valor.replace(/(\d{3})(\d)/, '$1.$2');
    valor = valor.replace(/(\d{3})(\d{1,2})$/, '$1-$2');

    input.value = valor;

}

if (cpfInput) {

    aplicarMascaraCpf(cpfInput);

    cpfInput.addEventListener('input', function () {

        aplicarMascaraCpf(this);

    });

}




const cnpjInput = document.getElementById('cnpj');

function aplicarMascaraCnpj(input) {

    let valor = input.value.replace(/\D/g, '');

    valor = valor.slice(0, 14);

    valor = valor.replace(/^(\d{2})(\d)/, '$1.$2');
    valor = valor.replace(/^(\d{2})\.(\d{3})(\d)/, '$1.$2.$3');
    valor = valor.replace(/\.(\d{3})(\d)/, '.$1/$2');
    valor = valor.replace(/(\d{4})(\d)/, '$1-$2');

    input.value = valor;

}

if (cnpjInput) {

    aplicarMascaraCnpj(cnpjInput);

    cnpjInput.addEventListener('input', function () {

        aplicarMascaraCnpj(this);

    });

}




const inscricaoEstadualInput = document.getElementById('inscricaoEstadual');

function aplicarMascaraInscricaoEstadual(input) {

    let valor = input.value.replace(/\D/g, '');

    valor = valor.slice(0, 14);

    input.value = valor;

}

if (inscricaoEstadualInput) {

    aplicarMascaraInscricaoEstadual(inscricaoEstadualInput);

    inscricaoEstadualInput.addEventListener('input', function () {

        aplicarMascaraInscricaoEstadual(this);

    });

}




const emailInput = document.getElementById('email');

if (emailInput) {

    emailInput.addEventListener('blur', function () {

        const email = this.value;

        const valido =
            email.includes('@') &&
            email.includes('.');

        if (!valido) {

            this.classList.add('is-invalid');

        } else {

            this.classList.remove('is-invalid');

        }

    });

}




const inputFoto = document.getElementById('foto');
const previewFoto = document.getElementById('previewFoto');

if (inputFoto && previewFoto) {

    inputFoto.addEventListener('change', function (event) {

        const arquivo = event.target.files[0];

        if (arquivo) {

            const reader = new FileReader();

            reader.onload = function (e) {

                previewFoto.src = e.target.result;
                previewFoto.style.display = 'block';

            };

            reader.readAsDataURL(arquivo);

        }

    });

}