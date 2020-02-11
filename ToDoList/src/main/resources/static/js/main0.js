$(function(){

    const appendTask = function(data) {
        var taskCode = '<a href="#" class="task-link" data-id="' +
            data.id + '">' + data.name + '</a><br>';
            $('#task-list')
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
         $.ajax({ // методом GET
                    method: "GET",
                    url: '/tasks/' + taskId,
                    success: function(response) // если успешно, то добавляем в appendtask и форму скрываем
                    {
                      var deleteLink = '<a href="#" class="delete-link" data-id="' + response.id + '">' + ' Удалить </a>';
                      var editLink = '<a href="#" class="edit-link" data-id="' + response.id + '">' + ' Заменить задачу </a>';
                      var patchLink = '<a href="#" class="patch-link" data-id="' + response.id + '">' + ' Изменить </a>';
                       var code = '<span>Описание: ' + response.description + '</span><br>';
                       link.parent().append(code).append(patchLink).append(editLink).append(deleteLink); // берем ссылку, родительский метод и добавляем в конец наш код
                    },
                    error: function(response)
                    {
                        if(response.status == 404) {
                            alert('Задача не найдена!');
                        }
                    }
                });
                return false; // чтобы не перезагрузилась страница
    });

    // DELETE TASK
    $(document).on('click', '.delete-link', function(){
            var link = $(this);
            var taskId = link.data('id'); // Получаем ID из первого блока
             $.ajax({ // методом DELETE
                        method: "DELETE",
                        url: '/tasks/' + taskId,
                        success: function(response) // если успешно, то добавляем в appendTask и форму скрываем
                        {
                           alert('Задача успешно удалена! Пожалуйста обновите страницу.');
                        },
                        error: function(response)
                        {
                            if(response.status == 404) {
                                alert('Задача не найдена!');
                            } else {
                            alert('Что-то пошло не так!'); }
                        }
                    });
                    return true; // чтобы не перезагрузилась страница
        });


    // Adding task
    $('#save-task').click(function() // заполнили форму и нажали Save task
    {
        var data = $('#task-form form').serialize(); // инфа попадает в js объект и отправляется по адресу tasks
        $.ajax({ // методом Post
            method: "POST",
            url: '/tasks/',
            data: data,
            success: function(response) // если успешно, то добавляем в appendTask и форму скрываем
            {
                $('#task-form').css('display', 'none');
                var task = {};
                task.id = response; // возвращает не объект а число
                var dataArray = $('#task-form form').serializeArray();
                for(i in dataArray) {
                    task[dataArray[i]['name']] = dataArray[i]['value'];
                }
                appendTask(task);
            }
        });
        return false;
    });

    // PUT Modify whole task
        $(document).on('click', '.edit-link', function(){
        var link = $(this);
        var taskId = link.data('id'); // Получаем ID из первого блока
        $('#task-put-form').css('display', 'flex'); // показываем форму редактирования для данного ID
        $('#edit-task').click(function() // заполнили форму и нажали Save task
            {
            var data = $('#task-put-form form').serialize(); // получаем данные из формы
               $.ajax({
                      method: "PUT",
                      url: '/tasks/' + taskId,
                      data: data,
                      success: function(response) // если успешно, то добавляем в appendTask и форму скрываем
                      {
                       alert('Задача успешно изменена! Обновите страницу');
                       $('#task-put-form').css('display', 'none');
                       var task = {};
                       task.id = response; // возвращает не объект а число
                       var dataArray = $('#task-put-form form').serializeArray();
                       for(i in dataArray) {
                           task[dataArray[i]['name']] = dataArray[i]['value'];
                           }
                       appendTask(task);
                       },
                       error: function(response)
                       {
                          if(response.status == 404) {
                             alert('Задача не найдена!');
                           } else {
                             alert('Что-то пошло не так!'); }
                           }
                        });
                        return false;
                });
                })

    // PATCH Modify task
            $(document).on('click', '.patch-link', function(){
            var link = $(this);
            var taskId = link.data('id'); // Получаем ID из первого блока
            $('#task-patch-form').css('display', 'flex'); // показываем форму редактирования для данного ID
            $('#patch-task').click(function() // заполнили форму и нажали Save task
                {
                var data = $('#task-patch-form form').serialize(); // получаем данные из формы
                   $.ajax({
                          method: "PATCH",
                          url: '/tasks/' + taskId,
                          data: data,
                          success: function(response) // если успешно, то добавляем в appendTask и форму скрываем
                          {
                           alert('Задача успешно изменена! Обновите страницу');
                           $('#task-patch-form').css('display', 'none');
                           var task = {};
                           task.id = response; // возвращает не объект а число
                           var dataArray = $('#task-patch-form form').serializeArray();
                           for(i in dataArray) {
                               task[dataArray[i]['name']] = dataArray[i]['value'];
                               }
                           appendTask(task);
                           },
                           error: function(response)
                           {
                              if(response.status == 404) {
                                 alert('Задача не найдена!');
                               } else {
                                 alert('Что-то пошло не так!'); }
                               }
                            });
                            return false;
                    });
                    })
})