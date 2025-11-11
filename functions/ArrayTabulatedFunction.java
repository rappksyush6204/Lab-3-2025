package functions;

//Класс хранит точки в упорядоченном массиве
public class ArrayTabulatedFunction implements TabulatedFunction {
    private FunctionPoint[] points;     // Массив для хранения точек
    private int pointsCount;           // Фактическое количество точек

    // КОНСТРУКТОРЫ:
    public ArrayTabulatedFunction(double leftX, double rightX, int pointsCount) throws IllegalArgumentException {
        // Проверка параметров
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница не может быть больше или равна правой");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек не может быть меньше двух");
        }

        this.pointsCount = pointsCount;
        // Создаем массив с запасом места для будущих добавлений
        this.points = new FunctionPoint[pointsCount + 2];

        // Вычисляем шаг между точками
        double step = (rightX - leftX) / (pointsCount - 1);
        
        // Создаем точки с равными интервалами
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;  // Вычисляем x координату
            points[i] = new FunctionPoint(x, 0.0);  // y = 0 по умолчанию
        }
    }

    //Вместо количества точек получает значения функции в виде массива
    public ArrayTabulatedFunction(double leftX, double rightX, double[] values) throws IllegalArgumentException {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница области не может быть больше или равна правой");
        }
        if (values.length < 2) {
            throw new IllegalArgumentException("Количество точек не может быть меньше двух");
        }
        
        this.pointsCount = values.length;
        this.points = new FunctionPoint[pointsCount + 2];

        double step = (rightX - leftX) / (pointsCount - 1);
        
        // Создаем точки с заданными значениями y
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            points[i] = new FunctionPoint(x, values[i]);  // Используем переданные значения y
        }
    }

    // МЕТОДЫ ДЛЯ РАБОТЫ С ФУНКЦИЕЙ
    public double getLeftDomainBorder() {
        return points[0].getX();  // Первая точка - самая левая
    }

    public double getRightDomainBorder() {
        return points[pointsCount - 1].getX();  // Последняя точка - самая правая
    }

    public double getFunctionValue(double x) {
        // Проверка что x в области определения
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;  // Не число - точка вне области определения
        }

        // Ищем интервал, в который попадает x
        for (int i = 0; i < pointsCount - 1; i++) {
            double x1 = points[i].getX();
            double x2 = points[i + 1].getX();
            
            // Проверяем совпадение с x1 (используя машинный эпсилон)
            if (Math.abs(x - x1) < 1e-10) {
                return points[i].getY();
            }
            
            // Проверяем совпадение с x2 (используя машинный эпсилон)
            if (Math.abs(x - x2) < 1e-10) {
                return points[i + 1].getY();
            }
            
            if (x > x1 && x < x2) {
                double y1 = points[i].getY();
                double y2 = points[i + 1].getY();
                
                // Линейная интерполяция
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
        }
        
        return Double.NaN;
    }

    // МЕТОДЫ ДЛЯ РАБОТЫ С ТОЧКАМИ:
    
    // Количество точек в функции
    public int getPointsCount() {
        return pointsCount;
    }

    //Возвращает копию точки (для инкапсуляции)
    public FunctionPoint getPoint(int index) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " выходит за границы набора точек");
        }
        return new FunctionPoint(points[index]);
    }

    // Заменяет точку на новую
    public void setPoint(int index, FunctionPoint point) throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " выходит за границы набора точек");
        }

        // Улучшенная проверка с машинным эпсилоном
        if (index > 0 && point.getX() <= points[index - 1].getX() + 1e-10) {
            throw new InappropriateFunctionPointException("Новая точка нарушает порядок: x должен быть больше " + points[index - 1].getX()); 
        }
        if (index < pointsCount - 1 && point.getX() >= points[index + 1].getX() - 1e-10) {
            throw new InappropriateFunctionPointException("Новая точка нарушает порядок: x должен быть меньше " + points[index + 1].getX());
        }

        points[index].setX(point.getX());
        points[index].setY(point.getY());
    }

    // Возвращает координату x точки по индексу
    public double getPointX(int index) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " выходит за границы набора точек");
        }
        return points[index].getX();
    }

    // Устанавливает новую координату x для точки
    public void setPointX(int index, double x) throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Точка с индексом " + index + " не существует. Доступные индексы: 0 до " + (pointsCount - 1));
        }

        // Улучшенная проверка с машинным эпсилоном
        if (index > 0 && x <= points[index - 1].getX() + 1e-10) {
            throw new InappropriateFunctionPointException("Новый X " + x + " должен быть больше чем " + points[index - 1].getX() + " (X левого соседа)");
        }
        if (index < pointsCount - 1 && x >= points[index + 1].getX() - 1e-10) {
            throw new InappropriateFunctionPointException("Новый X " + x + " должен быть меньше чем " + points[index + 1].getX() + " (X правого соседа)");
        }

        points[index].setX(x);
    }

    // Возвращает координату y точки по индексу
    public double getPointY(int index) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " выходит за границы набора точек");
        }
        return points[index].getY();
    }

    // Устанавливает новую координату y для точки
    public void setPointY(int index, double y) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " выходит за границы набора точек");
        }
        points[index].setY(y);
    }

    // МЕТОДЫ ДЛЯ ИЗМЕНЕНИЯ КОЛИЧЕСТВА ТОЧЕК:

    // Удаляет точку по индексу
    public void deletePoint(int index) throws FunctionPointIndexOutOfBoundsException, IllegalStateException {
        // Нельзя удалять если точек меньше 3 или индекс неверный
        if (pointsCount <= 2) {
            throw new IllegalStateException("Нельзя удалять точки: минимальное количество точек - 3");
        }
        
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " выходит за границы набора точек");
        }

        // Сдвигаем все элементы после удаляемой точки влево
        System.arraycopy(points, index + 1, points, index, pointsCount - index - 1);
        pointsCount--;  // Уменьшаем счетчик точек
    }

    // Добавляет новую точку в функцию (с сохранением упорядоченности)
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        // Проверяем что точка не пустая
        if (point == null) {
            throw new IllegalArgumentException("Точка не может быть null");
        }
        int insertIndex = 0;
        
        // Ищем позицию для вставки - где X новой точки больше текущего
        while (insertIndex < pointsCount) {
            double currentX = points[insertIndex].getX();
            // Используем машинный эпсилон для сравнения
            if (Math.abs(currentX - point.getX()) < 1e-10) {
                throw new InappropriateFunctionPointException("Точка с x=" + point.getX() + " уже существует в функции");
            }
            if (currentX > point.getX()) {
                break;
            }
            insertIndex++;
        }

        // Проверяем нужно ли увеличивать массив
        if (pointsCount >= points.length) {
            // Создаем новый массив в 2 раза больше
            FunctionPoint[] newPoints = new FunctionPoint[points.length * 2];
            System.arraycopy(points, 0, newPoints, 0, pointsCount);
            points = newPoints;
        }

        // Сдвигаем элементы чтобы освободить место для новой точки
        System.arraycopy(points, insertIndex, points, insertIndex + 1, pointsCount - insertIndex);
        
        // Вставляем новую точку (создаем копию для инкапсуляции)
        points[insertIndex] = new FunctionPoint(point);
        pointsCount++; 
    }
    
    // Строковое представление функции
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < pointsCount; i++) {
            result.append(points[i].toString());
            if (i < pointsCount - 1) {
                result.append("\n");
            }
        }
        return result.toString();
    }
}