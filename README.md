# quotes

#Виды data
- csv
- xml
- jdbc

#Виды tag
- init
- user
- security

#Виды бирж
- shares
- bonds

#Command
- insert - добавления пользователя
- get - получение пользователя по айди
- get_all - получение всех пользователей
- delete - удаление пользователя по айди
- update - обновление пользователя
- check_virtual_briefcase - Выводит информацию об акциях пользователя, который он сохранил в портфель.
- perform_action - Метод, который обрабатывает портфель пользователя(массив объектов класса акций) и исполняет указанное действие, который задал пользователь.
- find_security - Метод для поиска ценной бумаги по его названию или тикеру(кодовое обозначение актива на бирже).

#Шаблон функции
java -DconfigurationFile=log4j2.xml -Dconfig=enviroment.properties -jar quotes.jar **data** **tag** **command**  **args**


#Инициализация данных и авторизация как пользователь по айди 0
java -DconfigurationFile=log4j2.xml -Dconfig=environment.properties -jar quotes.jar csv init
#Метод для поиска ценной бумаги по его названию или тикеру(кодовое обозначение актива на бирже).
java -DconfigurationFile=log4j2.xml -Dconfig=environment.properties -jar quotes.jar csv security find_security sberbond
#Вывод активных ценных бумаг на бирже
java -DconfigurationFile=log4j2.xml -Dconfig=environment.properties -jar quotes.jar csv security find_security shares
#Выводит информацию об акциях пользователя, который он сохранил в портфель.
java -DconfigurationFile=log4j2.xml -Dconfig=environment.properties -jar quotes.jar csv user check_virtual_briefcase
#Добавление актива в портфель.
java -DconfigurationFile=log4j2.xml -Dconfig=environment.properties -jar quotes.jar csv user perform_action add sberbond
#Удаление актива в портфель.
java -DconfigurationFile=log4j2.xml -Dconfig=environment.properties -jar quotes.jar csv user perform_action delete sberbond
#Добавление пользователя
java -DconfigurationFile=log4j2.xml -Dconfig=environment.properties -jar quotes.jar csv user insert name
#Обновление пользователя по айди
java -DconfigurationFile=log4j2.xml -Dconfig=environment.properties -jar quotes.jar csv user update 0 dinh
#Удаление пользователя по айди
java -DconfigurationFile=log4j2.xml -Dconfig=environment.properties -jar quotes.jar csv user delete 0
#Получение пользователя по айди
java -DconfigurationFile=log4j2.xml -Dconfig=environment.properties -jar quotes.jar csv user get 0
#Получение всех пользователей
java -DconfigurationFile=log4j2.xml -Dconfig=environment.properties -jar quotes.jar csv user get_all
