// Функция для получения транзакций
function loadTransactions() {
    fetch(`/api/transactions`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            const tableBody = document.querySelector('#transactions-table tbody');
            tableBody.innerHTML = '';

            data.forEach(transaction => {

                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${transaction.name}</td>
                    <td>${transaction.amount}</td>
                    <td>${transaction.date}</td>
                    <td>${transaction.categoryName}</td>
                        <td>
                        <button class="btn btn-danger btn-sm" onclick="deleteTransaction('${transaction.id}')">
                            Delete
                        </button>
                    </td>
                `;
                tableBody.appendChild(row);
            });
        })
        .catch(error => {
            console.error('Error fetching transactions:', error);
        });
}

function checkLimit() {
    $.ajax({
        url: '/api/categories',  // URL для получения списка категорий
        method: 'GET',  // Тип запроса
        success: function (categories) {
            const categoryName = $('#transactionCategoryName').val();
            const category = categories.find(c => c.name === categoryName);
            if (category.excess) {
                    alert(`You have exceeded the budget limit for ${categoryName} category`);
                }
            }
    });
}

// Функция для загрузки категорий
function loadCategories() {
    $.ajax({
        url: '/api/categories',  // URL для получения списка категорий
        method: 'GET',  // Тип запроса
        success: function (categories) {
            var categorySelect = $('#transactionCategoryName');

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
            id: $('#id').val(),
            name: $('#name').val(),
            amount: $('#amount').val(),
            categoryName: $('#transactionCategoryName').val()
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
                checkLimit();
                return response.json();  // Преобразуем успешный ответ в JSON


            })
            .then(data => {
                // Успешный ответ, перенаправляем на главную страницу
                window.location.href = '/';
            })
            .catch(error => {
                // Проверяем, если ошибка содержит массив или текст
                if (Array.isArray(error)) {
                    // Если это массив ошибок, объединяем их в строку и отображаем через alert
                    alert(error.join('\n'));
                } else if (typeof error === 'string') {
                    // Если это текст, отображаем его напрямую
                    alert(error);
                } else {
                    // Если структура неизвестна, отображаем общее сообщение об ошибке
                    alert('An unexpected error occurred. Please try again.');
                }
            });
    });
});


function renderCategories() {
    const tableBody = document.querySelector('#categories-table tbody');
    tableBody.innerHTML = ''; // Очищаем таблицу перед добавлением

    // Запрос на получение категорий
    fetch('/api/categories')
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(categories => {
            // Перебираем полученные категории
            categories.forEach((category, index) => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${category.name}</td>
                    <td>${category.budgetLimit}</td>
                    <td>${category.spent}</td>
                    <td>${category.excess}</td>
                    <td>${category.type}</td>
                    <td>
                        <button class="btn btn-danger btn-sm" onclick="deleteCategory('${category.name}')">
                            Delete
                        </button>
                    </td>
                `;
                tableBody.appendChild(row);
            });
        }).catch(error => {
        // Проверяем, если ошибка содержит массив или текст
        if (Array.isArray(error)) {
            // Если это массив ошибок, объединяем их в строку и отображаем через alert
            alert(error.join('\n'));
        } else if (typeof error === 'string') {
            // Если это текст, отображаем его напрямую
            alert(error);
        } else {
            // Если структура неизвестна, отображаем общее сообщение об ошибке
            alert('An unexpected error occurred. Please try again.');
        }
    });
}

function deleteCategory(id) {
    if (confirm('Are you sure you want to delete this category?')) {
        fetch('/api/categories/' + id, {
            method: 'DELETE'
        })
            .then(response => {
                if (!response.ok) {
                    // Если ответ не успешный, пробуем извлечь тело ответа как JSON
                    return response.json().then(err => {
                        throw err; // Пробрасываем ошибку в блок catch
                    });
                }
            })
            .then(data => {
                console.log('Category deleted:', data);
                renderCategories();
                loadCategories();
            })
            .catch(error => {
                // Проверяем, если ошибка содержит массив ошибок
                if (Array.isArray(error)) {
                    // Если это массив ошибок, объединяем их в строку и отображаем через alert
                    alert(error.join('\n'));
                } else if (typeof error === 'string') {
                    // Если это текст ошибки, показываем его
                    alert(error);
                } else if (error.message) {
                    // Если это объект с сообщением, показываем его
                    alert(error.message);
                } else {
                    alert('An unexpected error occurred. Please try again.');
                }
            });
    }
}

function deleteTransaction(id) {
    if (confirm('Are you sure you want to delete this transaction?')) {
        fetch('/api/transactions/' + id, {
            method: 'DELETE'
        })
            .then(response => {
                if (!response.ok) {
                    // Преобразуем ошибку в JSON, если ответ не успешный
                    return response.json().then(err => {
                        throw err; // Выбрасываем ошибку для обработки в catch
                    });
                }
            })
            .then(() => {
                console.log('Transaction deleted');
                loadTransactions();
                loadCategories();
                renderCategories()
            })
            .catch(error => {
                if (Array.isArray(error)) {
                    // Если ошибка приходит в виде массива сообщений
                    alert(error.join('\n'));
                } else if (typeof error === 'string') {
                    // Если это строка ошибки
                    alert(error);
                } else if (error.message) {
                    // Если это объект с сообщением ошибки
                    alert(error.message);
                } else {
                    alert('An unexpected error occurred. Please try again.');
                }
            });
    }
}

$(document).ready(function () {
    // Обработчик отправки формы через AJAX
    $('#categoryForm').submit(function (event) {
        event.preventDefault(); // Предотвращаем стандартную отправку формы
        // Сбор данных формы
        const formData = {
            name: $('#categoryName').val(),
            budgetLimit: parseFloat($('#limit').val()),
            spent: 0,
            excess: false,
            type: $('#categoryType').val()
        };

        // Отправляем POST запрос с использованием fetch
        fetch('/api/categories', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(err => Promise.reject(err));
                }
                return response.json();
            })
            .then(data => {
                console.log('Category added successfully:', data);
                renderCategories();
                $('#categoryForm')[0].reset();
                loadCategories()
            })
            .catch(error => {
                // Проверяем, если ошибка содержит массив или текст
                if (Array.isArray(error)) {
                    // Если это массив ошибок, объединяем их в строку и отображаем через alert
                    alert(error.join('\n'));
                } else if (typeof error === 'string') {
                    // Если это текст, отображаем его напрямую
                    alert(error);
                } else {
                    // Если структура неизвестна, отображаем общее сообщение об ошибке
                    alert('An unexpected error occurred. Please try again.');
                }
            });

    });
});

$(document).ready(function () {
    // Function to format numbers to locale string
    const formatNumber = (number) => number?.toLocaleString() || 'N/A';

    // Function to create table rows dynamically
    const createTableRow = (columns) => `<tr>${columns.map(column => `<td>${column}</td>`).join('')}</tr>`;

    // Fetch budget data on page load
    $.ajax({
        url: '/api/budget',
        method: 'GET',
        contentType: 'application/json',
        success: function (data) {
            // Populate income and expense summaries
            $('#totalIncome').text(formatNumber(data.totalIncome));
            $('#totalExpense').text(formatNumber(data.totalExpense));

            // Populate income by category
            const incomeRows = data.incomes.map(income =>
                createTableRow([income.categoryName, formatNumber(income.sum)])
            ).join('');
            $('#incomeByCategory').html(incomeRows);

            // Populate budget by category
            const budgetRows = data.budgets.map(budget => {
                const remaining = formatNumber(budget.limit - budget.sum);
                return createTableRow([budget.categoryName, formatNumber(budget.limit), remaining]);
            }).join('');
            $('#budgetByCategory').html(budgetRows);
        },
        error: function (xhr, status, error) {
            console.error('Error fetching budget data:', xhr.responseText || error);
            alert('Failed to load budget data. Please try again later.');
        }
    });
});

$(document).ready(function () {
    $('#categoryType').on('change', function () {
        if ($(this).val() === 'EXPENSE') {
            $('#limitGroup').show();
            $('#limit').attr('required', 'required');
        } else {
            $('#limitGroup').hide();
            $('#limit').removeAttr('required');
        }
    });
});

$('#logoutBtn').click(function() {
    // Assuming you are using Spring Security for logout
    $.ajax({
        url: '/logout', // URL for logging out (adjust if necessary)
        type: 'POST',
        success: function() {
            window.location.href = '/login'; // Redirect to login page after logout
        },
        error: function() {
            alert('Error during logout');
        }
    });
});

document.addEventListener('DOMContentLoaded', renderCategories);

// Выполняем загрузку данных при готовности документа
$(document).ready(function () {
    loadTransactions();
    loadCategories();
});
