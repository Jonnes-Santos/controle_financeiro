package financeiro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Transacao {
    private String descricao;
    private double valor;
    private String data;

    // Construtor
    public Transacao(String descricao, double valor, String data) {
        if (descricao == null || descricao.isEmpty() || valor <= 0 || data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Dados inválidos para a transação.");
        }
        this.descricao = descricao;
        this.valor = valor;
        this.data = data;
    }

    // Getters
    public String getDescricao() {
        return descricao;
    }

    public double getValor() {
        return valor;
    }

    public String getData() {
        return data;
    }

    // Método utilitário para obter conexão com o banco de dados
    private static Connection obterConexao() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/financa_pessoal";
        String user = "root";
        String password = "019856";
        return DriverManager.getConnection(url, user, password);
    }

    // Método para inserir uma nova transação no banco de dados
    public void inserirNoBanco() {
        String query = "INSERT INTO transacoes (descricao, valor, data) VALUES (?, ?, ?)";
        try (Connection conn = obterConexao();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, descricao);
            stmt.setDouble(2, valor);
            stmt.setString(3, data);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro ao inserir transação: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método para buscar transações por mês
    public static List<Transacao> buscarTransacoesPorMes(int mes) {
        List<Transacao> transacoes = new ArrayList<>();
        String sql = "SELECT descricao, valor, data FROM transacoes WHERE MONTH(data) = ? ORDER BY data ASC";
        try (Connection connection = obterConexao();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, mes);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String descricao = resultSet.getString("descricao");
                double valor = resultSet.getDouble("valor");
                String data = resultSet.getString("data");

                transacoes.add(new Transacao(descricao, valor, data));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar transações: " + e.getMessage());
            e.printStackTrace();
        }

        return transacoes;
    }

    // Método para excluir uma transação com base na descrição
    public static void excluirTransacao(String descricao) {
        if (descricao == null || descricao.isEmpty()) {
            System.err.println("Descrição inválida.");
            return;
        }

        String query = "DELETE FROM transacoes WHERE descricao = ?";
        try (Connection conn = obterConexao();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, descricao);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Transação excluída com sucesso.");
            } else {
                System.out.println("Nenhuma transação encontrada com a descrição fornecida.");
            }

        } catch (SQLException e) {
            System.err.println("Erro ao excluir transação: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método para criar tabela se não existir
    public static void criarTabelaSeNaoExistir() {
        String query = "CREATE TABLE IF NOT EXISTS transacoes (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "descricao VARCHAR(255) NOT NULL, " +
                "valor DOUBLE NOT NULL, " +
                "data DATE NOT NULL)";
        try (Connection conn = obterConexao();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(query);
            System.out.println("Tabela verificada/criada com sucesso.");

        } catch (SQLException e) {
            System.err.println("Erro ao criar/verificar tabela: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
