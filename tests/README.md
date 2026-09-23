\# Тесты



На этапе 1 тестирование ручное. Сценарии:


```
| №| Ввод | Ожидаемый вывод |

| 1 | ls | ls без аргументов |

| 2 | ls -l "my folder" | аргументы -l, my folder |

| 3 | cd 'a b c' | аргумент a b c |

| 4 | foo | Error: unknown command: foo |

| 5 | ls "unclosed | Error: unclosed quote |

| 6 | exit | The session is over, выход |
```