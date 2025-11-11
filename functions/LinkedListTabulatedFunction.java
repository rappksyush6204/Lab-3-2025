package functions;

public class LinkedListTabulatedFunction implements TabulatedFunction {
    
    // внутренний класс для элемента списка 
    //(static-чтобы он не имел доступа к полям внешнего класса, private-чтобы был доступен только внутри класса)
    private static class FunctionNode {
        private FunctionPoint point;          // точки (значение x и y)
        private FunctionNode prev;            // ссылка на предыдущий узел в списке
        private FunctionNode next;            // ссылка на следующий узел 
        
        // конструктор с точкой
        FunctionNode(FunctionPoint point) {
            this.point = point;      // сохраняем переданную точку
            this.prev = null;        // пока нет связей - null
            this.next = null;        
        }
        
        // конструктор с точкой и связями
        FunctionNode (FunctionPoint point, FunctionNode prev, FunctionNode next) {
            this.point = point;
            this.prev = prev;   
            this.next = next;    
        }
        
        FunctionPoint getPoint() {
            return point;
        }

        void setPoint(FunctionPoint point) {
            this.point = point;  // устанавливаем новую точку
        }
        
        // геттер для предыдущего узла
        FunctionNode getPrev() {
            return prev;  // возвращаем ссылку на предыдущий узел
        }
        
        // сеттер для предыдущего узла
        void setPrev(FunctionNode prev) {
            this.prev = prev;  // устанавливаем ссылку на предыдущий узел
        }
        
        // геттер для следующего узла 
        FunctionNode getNext() {
            return next;
        }
        
        // сеттер для следующего узла
        void setNext(FunctionNode next) {
            this.next = next; 
        }
    }
    
    // поля основного класса
    private FunctionNode head;                  // голова списка
    private int pointsCount;                    // кол-во точек в списке
    private FunctionNode lastAccessedNode;      // последний элемент к которому обращались
    private int lastAccessedIndex;              // номер последнего элемента к которому обращались
  
    // конструктор по умолчанию
    public LinkedListTabulatedFunction() {
        initEmptyList();  // метод инициализации пустого списка
    }
    
    // конструктор с равномерным распределением точек
    public LinkedListTabulatedFunction(double leftX, double rightX, int pointsCount) {
        // проверка условий как в ArrayTabulatedFunction
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Должно быть не менее 2 точек");
        }
        
        initEmptyList(); // инициализируем пустой список
        
        // создаем точки с равномерным распределением
        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            double y = 0; // начальное значение Y
            
            // используем наш метод для добавления точки
            FunctionPoint point = new FunctionPoint(x, y);
            addNodeToTail().setPoint(point); // добавляем в конец и устанавливаем точку
        }
    }
    
    // конструктор с заданными значениями Y
    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values) {
        // проверка условий
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        if (values.length < 2) {
            throw new IllegalArgumentException("Должно быть не менее 2 точек");
        }
        
        initEmptyList(); // инициализируем пустой список
        
        // создаем точки с заданными значениями Y
        double step = (rightX - leftX) / (values.length - 1);
        for (int i = 0; i < values.length; i++) {
            double x = leftX + i * step;
            double y = values[i];
            
            // используем наш метод для добавления точки
            FunctionPoint point = new FunctionPoint(x, y);
            addNodeToTail().setPoint(point); // добавляем в конец и устанавливаем точку
        }
    }
    
    // инициализация пустого списка
    private void initEmptyList() {
        // создаем голову списка - специальный узел без данных
        head = new FunctionNode(null);
        // голова ссылается сама на себя
        head.setPrev(head);  // предыдущий от головы - сама голова
        head.setNext(head);  // следующий после головы - сама голова
        pointsCount = 0;     
        lastAccessedNode = head;      
        lastAccessedIndex = -1;       // индекс -1, так как нет реальных узлов
    }
    
    // метод для получения узла по индексу 
    private FunctionNode getNodeByIndex(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс за границами: " + index);
        }
        
        FunctionNode current;  // текущий узел
        int currentIndex;      // текущий индекс 
        
        // выбираем оптимальную точку старта для обхода
        if (lastAccessedNode != head && lastAccessedIndex != -1) {
            int distanceFromLast = Math.abs(index - lastAccessedIndex);  // расстояние от последнего доступного
            int distanceFromStart = index;                               // расстояние от начала
            int distanceFromEnd = pointsCount - 1 - index;               // расстояние от конца
            
            if (distanceFromLast <= distanceFromStart && distanceFromLast <= distanceFromEnd) {
                // стартуем от последнего доступного узла 
                current = lastAccessedNode;
                currentIndex = lastAccessedIndex;
            } else if (distanceFromStart <= distanceFromEnd) {
                // старт от начала 
                current = head.getNext(); 
                currentIndex = 0;
            } else {
                // старт от конца 
                current = head.getPrev();
                currentIndex = pointsCount - 1;
            }
        } else {
            current = head.getNext(); 
            currentIndex = 0;
        }
        
        // двигаемся к нужному узлу
        while (currentIndex != index) {
            if (currentIndex < index) {
                // вперед по списку
                current = current.getNext();
                currentIndex++;
            } else {
                // назад по списку
                current = current.getPrev();
                currentIndex--;
            }
        }
        
        // сохраняем найденный узел
        lastAccessedNode = current;
        lastAccessedIndex = currentIndex;
        
        return current;
    }
    
    // метод для добавления узла в конец списка
    private FunctionNode addNodeToTail() {
        FunctionPoint newPoint = new FunctionPoint(0, 0); // создаем новую точку с нулевыми координатами
        FunctionNode newNode = new FunctionNode(newPoint);      // создаем новый узел для этой точки
        FunctionNode tail = head.getPrev();
        
        // новый узел становится между tail и head
        
        // устанавливаем связи нового узла
        newNode.setPrev(tail);   // предыдущий для нового
        newNode.setNext(head);   // следующий после нового
        
        // обновляем связи соседних узлов
        tail.setNext(newNode);   // следующий после хвоста
        head.setPrev(newNode);   // предыдущий для головы
        
        pointsCount++;  // увеличиваем счетчик точек
        
        return newNode; 
    }
    
    // метод для добавления узла по индексу
    private FunctionNode addNodeByIndex(int index) {
        if (index < 0 || index > pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("индекс за границами: " + index);
        }
        
        // создаем новую точку
        FunctionPoint newPoint = new FunctionPoint(0, 0);
        // создаем новый узел для этой точки
        FunctionNode newNode = new FunctionNode(newPoint);
        
        // определяем где будем вставлять
        FunctionNode current;
        if (index == pointsCount) {
            // вставка в конец - перед головой
            current = head;
        } else {
            // вставка перед существующим узлом
            current = getNodeByIndex(index);
        }
        
        // узел который будет предыдущим для нового узла
        FunctionNode prevNode = current.getPrev();
        
        // newNode между prevNode и current
    
        newNode.setPrev(prevNode);  // предыдущий для нового - prevNode
        newNode.setNext(current);   // следующий после нового - current
        prevNode.setNext(newNode);  // следующий после prevNode - новый узел
        current.setPrev(newNode);   // предыдущий для current - новый узел
        
        pointsCount++;  
        
        lastAccessedNode = newNode;  // новый узел становится последним доступным
        lastAccessedIndex = index;   // сохраняем его индекс
        
        return newNode;  // возвращаем созданный узел
    }
    
    // метод для удаления узла по указанному индексу
    private FunctionNode deleteNodeByIndex(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("индекс за границами: " + index);
        }
        
        // находим узел для удаления
        FunctionNode nodeToDelete = getNodeByIndex(index);
        
        // получаем соседние узлы
        FunctionNode prevNode = nodeToDelete.getPrev();  
        FunctionNode nextNode = nodeToDelete.getNext(); 
    
        // Соединяем prevNode и nextNode напрямую, исключая nodeToDelete
        
        prevNode.setNext(nextNode);  // следующий после prevNode - nextNode
        nextNode.setPrev(prevNode);  // предыдущий для nextNode - prevNode
        
        pointsCount--;  
        
        if (lastAccessedNode == nodeToDelete) {
            lastAccessedNode = head;     
            lastAccessedIndex = -1;       // индекс сбрасываем
        }
        
        return nodeToDelete;
    }

    public int getPointsCount() {
        return pointsCount;  // просто возвращаем сохраненное значение
    }
    
    public FunctionPoint getPoint(int index) throws FunctionPointIndexOutOfBoundsException {
        FunctionNode node = getNodeByIndex(index);
        return new FunctionPoint(node.getPoint());  // возвращаем копию точки
    }
    
    public void setPoint(int index, FunctionPoint point) throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException {
        FunctionNode node = getNodeByIndex(index);
        
        // проверяем упорядоченность X с машинным эпсилоном
        if (index > 0 && point.getX() <= getNodeByIndex(index - 1).getPoint().getX() + 1e-10) {
            throw new InappropriateFunctionPointException("X точки должен быть больше предыдущего");
        }
        if (index < pointsCount - 1 && point.getX() >= getNodeByIndex(index + 1).getPoint().getX() - 1e-10) {
            throw new InappropriateFunctionPointException("X точки должен быть меньше следующего");
        }
        
        node.setPoint(new FunctionPoint(point));
    }
    
    public double getPointX(int index) throws FunctionPointIndexOutOfBoundsException {
        FunctionNode node = getNodeByIndex(index);
        return node.getPoint().getX();  // прямой доступ к X
    }
    
    public void setPointX(int index, double x) throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException {
        FunctionNode node = getNodeByIndex(index);
        FunctionPoint currentPoint = node.getPoint();
        
        // проверяем упорядоченность X с машинным эпсилоном
        if (index > 0 && x <= getNodeByIndex(index - 1).getPoint().getX() + 1e-10) {
            throw new InappropriateFunctionPointException("X точки должен быть больше предыдущего");
        }
        if (index < pointsCount - 1 && x >= getNodeByIndex(index + 1).getPoint().getX() - 1e-10) {
            throw new InappropriateFunctionPointException("X точки должен быть меньше следующего");
        }
        
        node.setPoint(new FunctionPoint(x, currentPoint.getY()));
    }
    
    public double getPointY(int index) throws FunctionPointIndexOutOfBoundsException {
        FunctionNode node = getNodeByIndex(index);
        return node.getPoint().getY();  // прямой доступ к Y
    }
    
    public void setPointY(int index, double y) throws FunctionPointIndexOutOfBoundsException {
        FunctionNode node = getNodeByIndex(index);
        FunctionPoint currentPoint = node.getPoint();
        
        // создаем новую точку с обновленным Y
        node.setPoint(new FunctionPoint(currentPoint.getX(), y));
    }
    
    public void deletePoint(int index) throws IllegalStateException, FunctionPointIndexOutOfBoundsException {
        // проверяем что после удаления останется минимум 2 точки
        if (pointsCount < 3) {
            throw new IllegalStateException("нельзя удалить точку: должно остаться минимум 2 точки");
        }
        
        // используем наш метод для удаления узла
        deleteNodeByIndex(index);
    }
    
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        // ищем позицию для вставки и проверяем на дубликат X
        int insertIndex = pointsCount;
        
        for (int i = 0; i < pointsCount; i++) {
            double currentX = getPointX(i);
            // Используем машинный эпсилон для сравнения вещественных чисел
            if (Math.abs(currentX - point.getX()) < 1e-10) {
                throw new InappropriateFunctionPointException("точка с таким X уже существует");
            }
            if (currentX > point.getX()) {
                insertIndex = i;
                break;
            }
        }
        
        // добавляем узел в найденную позицию
        FunctionNode newNode = addNodeByIndex(insertIndex);
        newNode.setPoint(new FunctionPoint(point));
    }
    
    public double getLeftDomainBorder() {
        if (pointsCount == 0) return Double.NaN;  // если нет точек
        return head.getNext().getPoint().getX();  // X первой точки
    }
    
    public double getRightDomainBorder() {
        if (pointsCount == 0) return Double.NaN;  // если нет точек
        return head.getPrev().getPoint().getX();  // X последней точки
    }
    
    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;  // если x вне области определения
        }
        
        // ищем отрезок для интерполяции
        for (int i = 0; i < pointsCount - 1; i++) {
            double x1 = getPointX(i);
            double x2 = getPointX(i + 1);
            
            // Проверяем совпадение с x1 (используя машинный эпсилон)
            if (Math.abs(x - x1) < 1e-10) {
                return getPointY(i);
            }
            
            // Проверяем совпадение с x2 (используя машинный эпсилон)
            if (Math.abs(x - x2) < 1e-10) {
                return getPointY(i + 1);
            }
            
            if (x > x1 && x < x2) {
                // линейная интерполяция
                double y1 = getPointY(i);
                double y2 = getPointY(i + 1);
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
        }
        
        return Double.NaN;
    }
}