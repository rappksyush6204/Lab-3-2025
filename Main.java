import functions.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Тестирование табулированной функции\n");
        
        // тестирование ArrayTabulatedFunction
        System.out.println("Array tabulated function:");
        testFunction(new ArrayTabulatedFunction(0, 4, 5), "Array");
        
        // тестирование LinkedListTabulatedFunction
        System.out.println("\nLinked list tabulated function:");
        testFunction(new LinkedListTabulatedFunction(0, 4, 5), "Linked List");
        
        // тестирование исключений
        System.out.println("\nТестирование исключений:");
        testExceptions();
        
        System.out.println("\nТестирование завершено");
    }
    
    static void testFunction(TabulatedFunction function, String type) {
        System.out.println("1. Создание " + type + " функции f(x) = x^2 на интервале [0, 4]:");
        
        // устанавливаем значения y = x^2 для каждой точки
        for (int i = 0; i < function.getPointsCount(); i++) {
            double x = function.getPointX(i);
            function.setPointY(i, x * x);  // y = x²
        }
        
        // выводим информацию о функции
        printFunctionInfo(function);
        
        // вычисление значений в разных точках
        System.out.println("\n2. Вычисление значений функции в различных точках:");
        double[] testPoints = {-1, 0, 0.5, 1, 1.5, 2, 2.5, 3, 3.5, 4, 5};
        
        for (double x : testPoints) {
            double y = function.getFunctionValue(x);
            if (Double.isNaN(y)) {
                System.out.printf("f(%.1f) = вне области определения\n", x);
            } else {
                System.out.printf("f(%.1f) = %.4f\n", x, y);
            }
        }
        
        // изменение точки
        System.out.println("\n3. Изменение точки с индексом 2:");
        System.out.println("До изменения: (" + function.getPointX(2) + ", " + function.getPointY(2) + ")");
        try {
            function.setPoint(2, new FunctionPoint(2.0, 3.0));
            System.out.println("После изменения: (" + function.getPointX(2) + ", " + function.getPointY(2) + ")");
        } catch (Exception e) {
            System.out.println("Ошибка при изменении точки: " + e.getMessage());
        }
        
        // добавление новой точки
        System.out.println("\n4. Добавление точки (1.5, 2.25):");
        try {
            function.addPoint(new FunctionPoint(1.5, 2.25));
            printFunctionInfo(function);
        } catch (Exception e) {
            System.out.println("Ошибка при добавлении точки: " + e.getMessage());
        }
        
        // удаление точки
        System.out.println("\n5. Удаление точки с индексом 1:");
        try {
            function.deletePoint(1);
            printFunctionInfo(function);
        } catch (Exception e) {
            System.out.println("Ошибка при удалении точки: " + e.getMessage());
        }
    }
    
    static void testExceptions() {
        System.out.println("Проверка работы исключений:");
        
        try {
            System.out.println("1. Левая граница больше правой:");
            new ArrayTabulatedFunction(10, 0, 5);
        } catch (IllegalArgumentException e) {
            System.out.println(" Ошибка: " + e.getMessage());
        }
        
        try {
            System.out.println("2. Меньше 2 точек:");
            new LinkedListTabulatedFunction(0, 10, 1);
        } catch (IllegalArgumentException e) {
            System.out.println(" Ошибка: " + e.getMessage());
        }
        
        try {
            System.out.println("3. Выход за границы массива:");
            ArrayTabulatedFunction func = new ArrayTabulatedFunction(0, 10, 3);
            func.getPoint(10);
        } catch (FunctionPointIndexOutOfBoundsException e) {
            System.out.println(" Ошибка:" + e.getMessage());
        }
        
        try {
            System.out.println("4. Нарушение упорядоченности X:");
            TabulatedFunction func = new LinkedListTabulatedFunction(0, 10, 3);
            // Пытаемся установить X за пределами допустимого диапазона
            func.setPointX(1, 15.0); // Должно быть между 5.0 и 10.0
        } catch (InappropriateFunctionPointException e) {
            System.out.println(" Ошибка: " + e.getMessage());
        }
        
        try {
            System.out.println("5. Удаление когда точек мало:");
            ArrayTabulatedFunction func = new ArrayTabulatedFunction(0, 10, 2);
            func.deletePoint(0);
        } catch (IllegalStateException e) {
            System.out.println(" Ошибка: " + e.getMessage());
        }
    }
    
    private static void printFunctionInfo(TabulatedFunction function) {
        System.out.println("Область определения: [" + function.getLeftDomainBorder() + 
                          ", " + function.getRightDomainBorder() + "]");
        System.out.println("Количество точек: " + function.getPointsCount());
        System.out.println("Точки функции:");
        
        for (int i = 0; i < function.getPointsCount(); i++) {
            try {
                double x = function.getPointX(i);
                double y = function.getPointY(i);
                System.out.printf("  %d: (%.2f, %.4f)\n", i, x, y);
            } catch (FunctionPointIndexOutOfBoundsException e) {
                System.out.println("  Ошибка доступа к точке " + i + ": " + e.getMessage());
            }
        }
    }
}