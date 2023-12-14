import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

// Compilação: javac matrizDual.java
// Execução: java matrizDual

class matrizDual {
    // Definição das matrizes e listas para o Problema do Transporte
    static int[][] costMatrix;
    static int[][] relativeCostMatrix;
    static int[][] transportMatrix;
    static ArrayList<Integer> supplyList = new ArrayList<>();
    static ArrayList<Integer> demandList = new ArrayList<>();
    static boolean isUnbalanced;
    static int[] zeroPosition = new int[2];

    // Método para ler o arquivo de entrada
    public static void readInput(String fileName) {
        try {
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);

            // Leitura da primeira linha do arquivo
            String[] dimensions = scanner.nextLine().split("\\s+");
            int m = Integer.parseInt(dimensions[0]);
            int n = Integer.parseInt(dimensions[1]);
            int totalSize = m + n;

            int totalSupply = 0, totalDemand = 0;
            for (int i = 0; i < totalSize; i++) {
                String data = scanner.nextLine();

                // Verifica se é oferta ou demanda
                if (i < m) {
                    supplyList.add(Integer.parseInt(data));
                    totalSupply += Integer.parseInt(data);
                } else {
                    demandList.add(Integer.parseInt(data));
                    totalDemand += Integer.parseInt(data);
                }
            }

            // Correção - caso oferta != demanda
            isUnbalanced = false;
            if (totalSupply != totalDemand) {
                isUnbalanced = true;
                int difference = totalDemand - totalSupply;
                supplyList.add(difference);
                m++;
            }

            costMatrix = new int[m][n];
            relativeCostMatrix = new int[m + 1][n + 1];
            transportMatrix = new int[m][n];

            for (int i = 0; i < m; i++) {
                if (i == (m - 1) && isUnbalanced) {
                    for (int x = 0; x < n; x++) {
                        costMatrix[i][x] = 0;
                        transportMatrix[i][x] = 0;
                    }
                } else {
                    String data = scanner.nextLine();
                    String[] aux = data.split("\\s+");
                    for (int x = 0; x < aux.length; x++) {
                        costMatrix[i][x] = Integer.parseInt(aux[x]);
                        transportMatrix[i][x] = 0;
                    }
                }
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Ocorreu um erro na leitura do arquivo.");
            e.printStackTrace();
        }
    }

    // Método para encontrar a solução do Problema do Transporte
    public static void findTransportationSolution() {
        int totalSupplyValue = 0;
        for (int i = 0; i < supplyList.size(); i++) {
            totalSupplyValue += supplyList.get(i);
        }

        int currentSupply = 0, currentDemand = 0;

        int i = 0;
        while (currentSupply < totalSupplyValue) {
            for (int j = 0; j < demandList.size(); j++) {
                if (demandList.get(j) < supplyList.get(i)) {
                    transportMatrix[i][j] = demandList.get(j);
                    currentSupply += demandList.get(j);

                    int remainingSupply = supplyList.get(i) - demandList.get(j);
                    demandList.set(j, 0);
                    supplyList.set(i, remainingSupply);
                } else {
                    transportMatrix[i][j] = supplyList.get(i);
                    currentSupply += supplyList.get(i);

                    int remainingDemand = demandList.get(j) - supplyList.get(i);
                    demandList.set(j, remainingDemand);
                    supplyList.set(i, 0);
                    break;
                }
            }
            i++;
        }

        boolean recalculate = true;
        zeroPosition[0] = zeroPosition[1] = -1;

        do {
            recalculate = formRelativeCostMatrix();
        } while (recalculate);
    }

    // Método para formar a Matriz de Custo Relativo
    public static boolean formRelativeCostMatrix() {
        for (int i = 0; i <= supplyList.size(); i++) {
            for (int j = 0; j <= demandList.size(); j++) {
                relativeCostMatrix[i][j] = 0;
            }
        }

        int u = 0, v = 0;

        for (int i = supplyList.size() - 1; i >= 0; i--) {
            for (int j = demandList.size() - 1; j >= 0; j--) {
                if (transportMatrix[i][j] != 0) {
                    v = relativeCostMatrix[supplyList.size()][j];
                    u = relativeCostMatrix[i][demandList.size()];

                    if (v != 0) {
                        u = costMatrix[i][j] - v;
                        relativeCostMatrix[i][demandList.size()] = u;
                    } else if (u != 0) {
                        v = costMatrix[i][j] - u;
                        relativeCostMatrix[supplyList.size()][j] = v;
                    } else {
                        u = costMatrix[i][j] - v;
                        relativeCostMatrix[i][demandList.size()] = u;
                    }
                } else {
                    if (zeroPosition[0] != -1 && zeroPosition[0] == i && zeroPosition[1] == j) {
                        v = relativeCostMatrix[supplyList.size()][j];
                        u = relativeCostMatrix[i][demandList.size()];

                        if (v != 0) {
                            u = costMatrix[i][j] - v;
                            relativeCostMatrix[i][demandList.size()] = u;
                        } else if (u != 0) {
                            v = costMatrix[i][j] - u;
                            relativeCostMatrix[supplyList.size()][j] = v;
                        } else {
                            u = costMatrix[i][j] - v;
                            relativeCostMatrix[i][demandList.size()] = u;
                        }
                    } else {
                        relativeCostMatrix[i][j] = -1;
                    }
                }
            }

            int count = 0;
            for (int a = 0; a < supplyList.size(); a++) {
                for (int x = 0; x < demandList.size(); x++) {
                    if (transportMatrix[a][x] == 0) {
                        count++;
                    }
                }
            }

            int basicSolution = supplyList.size() + demandList.size() - 1;

            if (count != basicSolution) {
                if (transportMatrix[supplyList.size() - 1][demandList.size() - 1] != 0) {
                    if (transportMatrix[supplyList.size() - 1][demandList.size() - 2] == 0) {
                        relativeCostMatrix[supplyList.size() - 1][demandList.size() - 2] = 0;
                    } else {
                        relativeCostMatrix[supplyList.size() - 1][demandList.size() - 2] = 0;
                    }
                }
            }
        }

        boolean hasNegative = false;
        int posI = 0, posJ = 0;
        int bottleneckI = 0, bottleneckJ = 0;

        for (int i = 0; i < supplyList.size(); i++) {
            for (int j = 0; j < demandList.size(); j++) {
                if (relativeCostMatrix[i][j] != 0) {
                    v = relativeCostMatrix[supplyList.size()][j];
                    u = relativeCostMatrix[i][demandList.size()];

                    int value = costMatrix[i][j] - (u + v);

                    if (isUnbalanced && i == supplyList.size() - 1) {
                        relativeCostMatrix[i][j] = value * (-1);
                    } else if (value < 0) {
                        hasNegative = true;
                        posI = i;
                        posJ = j;
                        relativeCostMatrix[i][j] = value;
                    } else {
                        relativeCostMatrix[i][j] = value;
                    }
                }
            }
        }

        if (hasNegative) {
            boolean finished = false;
            int squares[][] = new int[4][2];
            int min = 0;

            squares[0][0] = posI;
            squares[0][1] = posJ;

            for (int i = 0; i < supplyList.size() && !finished; i++) {
                if (transportMatrix[i][posJ] != 0) {
                    squares[1][0] = i;
                    squares[1][1] = posJ;

                    for (int j = 0; j < demandList.size(); j++) {
                        if (transportMatrix[i][j] != 0) {
                            if (transportMatrix[posI][j] != 0) {
                                finished = true;
                                squares[2][0] = i;
                                squares[2][1] = j;
                                squares[3][0] = posI;
                                squares[3][1] = j;

                                if (transportMatrix[i][posJ] < transportMatrix[posI][j]) {
                                    min = transportMatrix[i][posJ];
                                    bottleneckI = i;
                                    bottleneckJ = posJ;
                                } else {
                                    min = transportMatrix[posI][j];
                                    bottleneckI = posI;
                                    bottleneckJ = j;
                                }
                            }
                        }
                    }
                }
            }

            for (int i = 0; i < 4; i++) {
                int aux1, aux2;
                aux1 = squares[i][0];
                aux2 = squares[i][1];
                if (i % 2 == 0) {
                    transportMatrix[aux1][aux2] = transportMatrix[aux1][aux2] + min;
                } else {
                    transportMatrix[aux1][aux2] = transportMatrix[aux1][aux2] - min;
                    if (aux1 != bottleneckI && aux2 != bottleneckJ && transportMatrix[aux1][aux2] == 0) {
                        zeroPosition[0] = aux1;
                        zeroPosition[1] = aux2;
                    }
                }
            }
        }
        return hasNegative;
    }

    // Método para calcular o custo total do transporte
    public static void calculateTotalTransportationCost() {
        int totalCost = 0;
        for (int i = 0; i < supplyList.size(); i++) {
            for (int j = 0; j < demandList.size(); j++) {
                totalCost += costMatrix[i][j] * transportMatrix[i][j];
            }
        }
        System.out.println("Custo total de transporte: " + totalCost);
    }

    // Método principal
    public static void main(String[] args) throws java.lang.Exception {
        long startTime = System.currentTimeMillis();

        readInput("teste2.txt");
        findTransportationSolution();
        calculateTotalTransportationCost();

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        System.out.println("Tempo de execução: " + executionTime + " milissegundos");
    }
}
