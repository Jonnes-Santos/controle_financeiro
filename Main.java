package financeiro;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class Main extends Application {

    private ComboBox<String> comboMes;
    private TextArea areaTexto;

    @Override
    public void start(Stage primaryStage) {
        Text title = new Text("Controle de Finanças ");
        title.setFont(Font.font("Hack", 30));
        title.setFill(Color.WHITE);
        title.setStyle("-fx-font-weight: bold;");

        // Botão para inserir transação
        Button btnInserir = new Button("Inserir Gastos");
        btnInserir.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px;");
        btnInserir.setOnAction(e -> abrirJanelaInserir());

        // Botão para exibir transações
        Button btnMostrar = new Button("Mostrar Gastos");
        btnMostrar.setStyle("-fx-background-color: #008CBA; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px;");
        btnMostrar.setOnAction(e -> exibirTransacoes());

        // Botão para excluir transação
        Button btnExcluir = new Button("Excluir Histórico");
        btnExcluir.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px;");
        btnExcluir.setOnAction(e -> abrirJanelaExcluir());

        // HBox para os botões à esquerda
        HBox hboxButtons = new HBox(10, btnInserir, btnMostrar, btnExcluir);
        hboxButtons.setAlignment(Pos.CENTER_LEFT);

        // ComboBox para selecionar o mês
        comboMes = new ComboBox<>();
        comboMes.getItems().addAll(
                "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
                "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
        );
        comboMes.setValue("Janeiro");
        comboMes.setStyle("-fx-font-size: 14px;");

        // HBox para o ComboBox à direita
        Label labelMes = new Label("Selecione o mês:");
        labelMes.setTextFill(Color.WHITE);
        HBox hboxMes = new HBox(labelMes, comboMes);
        hboxMes.setAlignment(Pos.CENTER_RIGHT);
        hboxMes.setSpacing(10);

        // Layout para os controles
        BorderPane controlesPane = new BorderPane();
        controlesPane.setLeft(hboxButtons);
        controlesPane.setRight(hboxMes);
        controlesPane.setPadding(new Insets(10));

        // TextArea para exibir as transações
        areaTexto = new TextArea();
        areaTexto.setEditable(false);
        areaTexto.setStyle("-fx-font-family: 'Monospaced'; -fx-font-size: 14px;");
        areaTexto.setPrefHeight(200);

        // VBox para os elementos principais
        VBox vbox = new VBox(10, title, controlesPane, areaTexto);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); -fx-padding: 20px;");

        Scene scene = new Scene(vbox, 600, 400);
        primaryStage.setTitle("Controle de Finanças | por @john1santoz");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Método para exibir transações com soma dos valores
    private void exibirTransacoes() {
        int mesSelecionado = getNumeroMes(comboMes.getValue());
        List<Transacao> transacoes = Transacao.buscarTransacoesPorMes(mesSelecionado);

        if (transacoes.isEmpty()) {
            areaTexto.setText("Nenhuma transação encontrada para o mês selecionado.");
            return;
        }

        // Construção de tabela formatada
        StringBuilder mensagem = new StringBuilder();
        mensagem.append(String.format("%-20s %-10s %-10s%n", "Descrição", "Valor", "Data"));
        mensagem.append("=".repeat(40)).append("\n");

        double somaValores = 0.0;

        for (Transacao t : transacoes) {
            mensagem.append(String.format("%-20s %-10.2f %-10s%n",
                    t.getDescricao(), t.getValor(), t.getData()));
            somaValores += t.getValor(); // Acumula o valor da transação
        }

        // Adiciona a soma ao final da tabela
        mensagem.append("=".repeat(40)).append("\n");
        mensagem.append(String.format("Total dos Gastos: %.2f%n", somaValores));

        areaTexto.setText(mensagem.toString());
    }

    // Método para mapear o nome do mês para o número do mês
    private int getNumeroMes(String mes) {
        switch (mes) {
            case "Janeiro": return 1;
            case "Fevereiro": return 2;
            case "Março": return 3;
            case "Abril": return 4;
            case "Maio": return 5;
            case "Junho": return 6;
            case "Julho": return 7;
            case "Agosto": return 8;
            case "Setembro": return 9;
            case "Outubro": return 10;
            case "Novembro": return 11;
            case "Dezembro": return 12;
            default: return 1;
        }
    }

    // Janela para excluir transação
    private void abrirJanelaExcluir() {
        Stage excluirStage = new Stage();
        excluirStage.setTitle("Excluir Gastos");

        TextField descricaoField = new TextField();
        descricaoField.setPromptText("Digite a descrição do gasto");

        Button btnExcluir = new Button("Excluir");
        btnExcluir.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        btnExcluir.setOnAction(e -> {
            try {
                String descricao = descricaoField.getText();
                Transacao.excluirTransacao(descricao);
                descricaoField.clear(); // Limpa o campo após a exclusão
            } catch (Exception ex) {
                ex.printStackTrace(); // Você pode exibir uma mensagem de erro aqui
            }
        });

        VBox vbox = new VBox(10, descricaoField, btnExcluir);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: black; -fx-padding: 20px;");

        Scene scene = new Scene(vbox, 300, 150);
        excluirStage.setScene(scene);
        excluirStage.show();
    }

    // Janela para inserir transação (com DatePicker)
    private void abrirJanelaInserir() {
        Stage inserirStage = new Stage();
        inserirStage.setTitle("Inserir Gastos");

        TextField descricaoField = new TextField();
        descricaoField.setPromptText("Descrição");

        TextField valorField = new TextField();
        valorField.setPromptText("Valor");

        // Adicionando o DatePicker para selecionar a data
        DatePicker dataPicker = new DatePicker();
        dataPicker.setValue(LocalDate.now()); // Definir a data atual como padrão

        Button btnSalvar = new Button("Salvar");
        btnSalvar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnSalvar.setOnAction(e -> {
            try {
                String descricao = descricaoField.getText();
                double valor = Double.parseDouble(valorField.getText());
                LocalDate data = dataPicker.getValue();

                // Convertendo a data selecionada para o formato String (yyyy-MM-dd)
                String dataString = data.toString();

                Transacao novaTransacao = new Transacao(descricao, valor, dataString);
                novaTransacao.inserirNoBanco();

                descricaoField.clear();
                valorField.clear();
                dataPicker.setValue(LocalDate.now()); // Resetar o DatePicker para a data atual

                inserirStage.close();
            } catch (Exception ex) {
                ex.printStackTrace(); // Você pode exibir uma mensagem de erro aqui
            }
        });

        VBox vbox = new VBox(10, descricaoField, valorField, dataPicker, btnSalvar);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: black; -fx-padding: 20px;");

        Scene scene = new Scene(vbox, 300, 250);
        inserirStage.setScene(scene);
        inserirStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}