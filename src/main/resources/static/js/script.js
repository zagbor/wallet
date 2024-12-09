// Функция для получения транзакций
function loadTransactions() {// Получаем имя пользователя динамически
    fetch(`/api/transactions`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            const tableBody = document.querySelector('#transactions-table tbody');
            tableBody.innerHTML = '';  // Очищаем текущие данные в таблице

            data.forEach(transaction => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${transaction.name}</td>
                    <td>${transaction.amount}</td>
                    <td>${transaction.date}</td>
                    <td>${transaction.category}</td>
                `;
                tableBody.appendChild(row);
            });
        })
        .catch(error => {
            console.error('Error fetching transactions:', error);
        });
}

// Функция для загрузки категорий
function loadCategories() {
    $.ajax({
        url: '/api/categories',  // URL для получения списка категорий
        method: 'GET',  // Тип запроса
        success: function (categories) {
            var categorySelect = $('#categoryName');

            // Очистить существующие опции
            categorySelect.empty();

            // Добавить пустую опцию
            categorySelect.append('<option value="">Select Category</option>');

            // Добавить новые категории
            if (categories.length === 0) {
                categorySelect.append('<option value="">No categories available</option>');
            } else {
                categories.forEach(function (category) {
                    categorySelect.append('<option value="' + category.name + '">' + category.name + '</option>');
                });
            }
        },
        error: function () {
            console.error('Error loading categories');
        }
    });
}

$(document).ready(function () {
    // Обработчик отправки формы через AJAX
    $('#transactionForm').submit(function (event) {
        event.preventDefault(); // Предотвращаем стандартную отправку формы

        // Сбор данных формы
        var formData = {
            name: $('#name').val(),
            amount: $('#amount').val(),
            type: $('#type').val(),
            date: new Date().toISOString(),  // Форматируем дату как 'YYYY-MM-DD'
            category: $('#categoryName').val()
        };

        // Отправляем POST запрос с использованием fetch
        fetch('/api/transactions', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(err => Promise.reject(err));  // Преобразуем ошибку в JSON и отклоняем промис
                }
                return response.json();  // Преобразуем успешный ответ в JSON
            })
            .then(data => {
                // Успешный ответ, перенаправляем на главную страницу
                window.location.href = '/';
            })
            .catch(error => {
                // Обработка ошибок
                if (error && error.errors) {
                    error.errors.forEach(errorMessage => {
                        console.log('Validation Error:', errorMessage);
                        // Здесь можно отобразить ошибки в интерфейсе
                    });
                } else {
                    console.log('Error:', error);
                    // Можно отобразить глобальную ошибку
                }
            });
    });
});


// Выполняем загрузку данных при готовности документа
$(document).ready(function () {
    loadTransactions();
    loadCategories();
});
