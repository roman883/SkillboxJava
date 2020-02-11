$(function()){

    const appendTask = function(data) {
//     var taskCode = '<h4' + data.name + '</h4>' +
//                'Год выпуска: ' + data.description;
//                $('#task-list')                                 // ДОБАВЛЯЕТ СЮДА
//                .append('<div>' + taskCode + '</div>');
//        };
        var taskCode = '<a href="#" class="task-link" data-id="' +
            data.id + '">' + data.name + '</a><br>';
            $('#task-list')                                 // ДОБАВЛЯЕТ СЮДА
            .append('<div>' + taskCode + '</div>');
    };

    // Loading tasks on load page МЕТОД ГЕТ ЗАПРАШИВАЕТ УРЛ БУКС И ПОЛУЧАЕТ СПИСОК КНИГ и ДОБАВЛЯЕТ КАЖДУЮ В БУК ЛИСТ (АБЗАЦ 1)
    $.get('/tasks/', function(response)
    {
        for(i in response) {
            appendTask(response[i]);
        }
    });

    // Show adding task form
    $('#show-add-task-form').click(function(){ // Если нажимаем на кнопку добавить книгу
        $('#task-form').css('display', 'flex'); // то показываем форму
    });

    // Closing adding task form
    $('#task-form').click(function(event){
        if(event.target === this) { // Если кликаем вне формы, то форма скрывается
            $(this).css('display', 'none');
        }
    });

    // Getting task (Обработчик всего документа, так как обновление динамически идет
    $(document).on('click', '.task-link', function(){
        var link = $(this);
        var taskId = link.data('id'); // Получаем ID из первого блока
         $.ajax({ // методом Post
                    method: "GET",
                    url: '/tasks/' + taskId,
                    success: function(response) // если успешно, то добавляем в appendtask и форму скрываем
                    {
                       var code = '<span>Описание: ' + response.description'</span>';
                       link.parent().append(code); // берем ссылку, родительский метод и добавляем в конец наш код
                    }
                    error: function(response)
                    {
                        if(response.status == 404) {
                            alert('Задача не найдена!');
                        }
                    }
                });
                return false; // чтобы не перезагрузилась страница
    });

    // Adding task
    $('#save-task').click(function() // заполнили форму и нажали Save task
    {
        var data = $('#task-form form').serialize(); // инфа попадает в js объект и отправляется по адресу tasks
        $.ajax({ // методом Post
            method: "POST",
            url: '/tasks/',
            data: data,
            success: function(response) // если успешно, то добавляем в appendtask и форму скрываем
            {
                $('#task-form').css('display', 'none');
                var task = {};
                task.id = response; // возвращает не объект а число
                var dataArray = $('#task-form form'),serializeArray();
                for(i in dataArray) {
                    task[dataArray[i]['name']] = dataArray[i]['value'];
                }
                appendTask(task);
            }
        });
        return false;
    });
});