# Штука, которая могла бы получать числа на вход

# Задача 
Разработайте на Java такую штуку, которая могла бы получать числа на вход.
И чтобы в любой момент времени эта штука могла ответить на следующие вопросы:

1. Какое из полученных на данный момент чисел самое малое?
2. Какое — самое большое?
3. Каково среднее всех полученных чисел?

Докажите, что эта штука работает корректно. Сделайте так, чтобы начинающий программист не смог пользоваться этой штукой как-то неправильно, и чтобы злой программист не смог эту штуку сломать.

# Пояснения
Обычно такая постановка задачи влечет целую серию интенсивных консультаций на тему "что конкретно?", с уточнением требований и ограничений.
Но в тестовых заданиях почему-то неявно считается, что кандидат должен сам додумать контекст и сделать допущения.
Делаю допущения, хотя наверняка не угадаю, что именно подразумевал автор:
  1. Под штукой имеется ввиду класс/библиотека для использования в виде программного компонента в других приложениях. А не отдельный продукт или сервис или API.
  1. Под числами подразумеваются любые числа примитивных типов.
  1. Под "получать на вход" подразумевается обрабатывать последовательность любой длины, в разумных пределах. Например выдержать поток 1000 чисел в секунду в течениии 10 лет (получается длина 3.15E+11).
  1. Под "доказательством корректной работы" подразумевается тестирование, включающее проверку граничных случаев. Не подразумавается математически строгое доказательство.
  1. Под защитой от "начинающего программиста" подразумавается простая защита от некорректных входных данных и некорректной последовательности вызова методов.
  1. Под защитой от "злого программиста" не знаю что подразумевать.
  Ну закрыл я класс от наследования, чтоб нельзя было в наследнике реализовать "зловред" и выдать куда-то наверх в качестве изначального типа.
  Ну обявил я поля как private. Но это все обходиться через Reflection.

Исходя из допущений и решения:
 1. реализовано в виде класса.
 1. реализовано для типа double, как наиболее универсального в данном случае.
 1. Проблема в вычислении среднего. Реализация прямо по мат.определению среднего - это накапливать сумму всех элеметов, которых может быть очень много.
 Double может быть легко переполнен даже на паре входных чисел.
 Тут либо аккумулировать сумму в BigNumber, но это медленно.
 Либо подсчитывать Avarage итеративно, без хранения суммы, но это потеря точности, и чем длиньше последовательность, тем больше потеря точности.
 Либо делить последовательность на кластеры и подсчитывать (и хранить) avarage для каждого кластера, выполняя финальный расчет по запросу.
 Я реализовал первые два варианта.
 1. написал серию юнит-тестов.
 1. реализовал проверку входных значений и состояния класса.
 В медленном-точном варианте сделал метод "putNext()" synchronized, чтоб начинающий программист не испортил всё конкурентным вызовом.
 1. Если не разнести "злого программиста" и эту штуку по разным ClassLoaders, то ее можно разнообразно сломать. 

# Implementation
в файлах:
- artsv.play.a.StreamStatFast
- artsv.play.a.StreamStatPrecise

Unit tests:
- artsv.play.a.StreamStatFastTest
- artsv.play.a.StreamStatPreciseTest

## Requirements
* Java 1.8
* JUnit 5.4 - for unit tests only (required to be added to dependencies)

