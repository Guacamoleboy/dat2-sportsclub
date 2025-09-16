// Package
package persistence;

// Imports
import entities.Member;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberMapper {

    // Attributes
    private Database database;

    // ________________________________________________

    public MemberMapper(Database database) {
        this.database = database;
    }

    // ________________________________________________

    public List<Member> getAllMembers() throws DatabaseException {
        List<Member> memberList = new ArrayList<>();

        // Query
        String sql = "SELECT member_id, name, address, m.zip, gender, city, year " +
                "FROM member m INNER JOIN zip USING(zip) ORDER BY member_id";

        try (Connection connection = database.getConnection();

             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int memberId = rs.getInt("member_id");
                String name = rs.getString("name");
                String address = rs.getString("address");
                int zip = rs.getInt("zip");
                String city = rs.getString("city");
                String gender = rs.getString("gender");
                int year = rs.getInt("year");

                memberList.add(new Member(memberId, name, address, zip, city, gender, year));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved hentning af medlemmer", e);
        }

        return memberList;
    }

    // ________________________________________________

    public Member getMemberById(int memberId) throws DatabaseException {
        Member member = null;

        // Query
        String sql = "SELECT member_id, name, address, m.zip, gender, city, year " +
                "FROM member m INNER JOIN zip USING(zip) WHERE member_id = ?";

        try (Connection connection = database.getConnection();

             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    String address = rs.getString("address");
                    int zip = rs.getInt("zip");
                    String city = rs.getString("city");
                    String gender = rs.getString("gender");
                    int year = rs.getInt("year");

                    member = new Member(memberId, name, address, zip, city, gender, year);
                }
            }

        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved hentning af medlem med id " + memberId, e);
        }

        return member;
    }

    // ________________________________________________

    public boolean deleteMember(int memberId) throws DatabaseException {

        // Query
        String sql = "DELETE FROM member WHERE member_id = ?";

        try (Connection connection = database.getConnection();

             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, memberId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected == 1;

        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved sletning af medlem med id " + memberId, e);
        }
    }

    // ________________________________________________

    public Member insertMember(Member member) throws DatabaseException {

        // Query
        String sql = "INSERT INTO member (name, address, zip, gender, year) VALUES (?,?,?,?,?)";

        try (Connection connection = database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, member.getName());
            ps.setString(2, member.getAddress());
            ps.setInt(3, member.getZip());
            ps.setString(4, member.getGender());
            ps.setInt(5, member.getYear());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1) {
                return null;
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    member.setMemberId(generatedKeys.getInt(1));
                } else {
                    return null;
                }
            }

        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved indsættelse af medlem", e);
        }

        return member;
    }

    // ________________________________________________

    public boolean updateMember(Member member) throws DatabaseException {

        // Query
        String sql = "UPDATE member SET name = ?, address = ?, zip = ?, gender = ?, year = ? WHERE member_id = ?";

        try (Connection connection = database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, member.getName());
            ps.setString(2, member.getAddress());
            ps.setInt(3, member.getZip());
            ps.setString(4, member.getGender());
            ps.setInt(5, member.getYear());
            ps.setInt(6, member.getMemberId());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected == 1;

        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved opdatering af medlem med id " + member.getMemberId(), e);
        }
    }

    // ________________________________________________

    public List<String> membersPerTeam() throws DatabaseException {
        List<String> teamCounts = new ArrayList<>();

        // Query
        String sql = "SELECT t.team_id, s.sport, COUNT(r.member_id) AS participant_count " +
                "FROM team t " +
                "JOIN sport s ON t.sport_id = s.sport_id " +
                "LEFT JOIN registration r ON t.team_id = r.team_id " +
                "GROUP BY t.team_id, s.sport " +
                "ORDER BY t.team_id";

        try (Connection connection = database.getConnection();

             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String teamId = rs.getString("team_id");
                String sport = rs.getString("sport");
                int count = rs.getInt("participant_count");
                teamCounts.add("Team " + teamId + " " + sport + " " + count + " members");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException("Error Per Team: ", e);
        }

        return teamCounts;
    }

    // ________________________________________________

    public List<String> membersPerSport() throws DatabaseException {
        List<String> result = new ArrayList<>();

        // Query
        String sql = "SELECT s.sport, COUNT(DISTINCT r.member_id) AS participants " +
                "FROM sport s " +
                "JOIN team t ON s.sport_id = t.sport_id " +
                "JOIN registration r ON t.team_id = r.team_id " +
                "GROUP BY s.sport ORDER BY s.sport";

        try (Connection connection = database.getConnection();

             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String sport = rs.getString("sport");
                int count = rs.getInt("participants");
                result.add(sport + ": " + count + " participants");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error per Sport: ", e);
        }

        return result;
    }

    // ________________________________________________

    public List<String> membersByGender() throws DatabaseException {
        List<String> result = new ArrayList<>();

        // Query
        String sql = "SELECT gender, COUNT(*) AS antal FROM member GROUP BY gender";

        try (Connection connection = database.getConnection();

             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String gender = rs.getString("gender");
                int count = rs.getInt("antal");
                result.add((gender.equals("m") ? "Men" : "Women") + ": " + count);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error by Gender: ", e);
        }

        return result;
    }

    // ________________________________________________

    public List<String> totalIncomeAllTeams() throws DatabaseException {
        List<String> result = new ArrayList<>();

        // Query
        String sql = "SELECT SUM(price) AS total_income FROM registration";

        try (Connection connection = database.getConnection();

             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                int total = rs.getInt("total_income");
                result.add("Total income: " + total + " kr.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error earnings: ", e);
        }

        return result;
    }

    // ________________________________________________

    public List<String> totalIncomePerTeam() throws DatabaseException {
        List<String> result = new ArrayList<>();

        // Query
        String sql = "SELECT t.team_id, s.sport, SUM(r.price) AS team_income " +
                "FROM team t " +
                "JOIN sport s ON t.sport_id = s.sport_id " +
                "JOIN registration r ON t.team_id = r.team_id " +
                "GROUP BY t.team_id, s.sport ORDER BY t.team_id";

        try (Connection connection = database.getConnection();

             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String teamId = rs.getString("team_id");
                String sport = rs.getString("sport");
                int income = rs.getInt("team_income");
                result.add("Team " + teamId + " " + income + " kr.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error earnings per team: ", e);
        }

        return result;
    }

    // ________________________________________________

    public List<String> averagePaymentPerTeam() throws DatabaseException {
        List<String> result = new ArrayList<>();

        // Query
        String sql = "SELECT t.team_id, s.sport, AVG(r.price) AS avg_payment " +
                "FROM team t " +
                "JOIN sport s ON t.sport_id = s.sport_id " +
                "JOIN registration r ON t.team_id = r.team_id " +
                "GROUP BY t.team_id, s.sport ORDER BY t.team_id";

        try (Connection connection = database.getConnection();

             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String teamId = rs.getString("team_id");
                String sport = rs.getString("sport");
                double avg = rs.getDouble("avg_payment");
                result.add(teamId + " - " + sport + ": " + avg + " kr.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error avg payment per team: ", e);
        }

        return result;
    }

} // MemberMapper End