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

} // MemberMapper End