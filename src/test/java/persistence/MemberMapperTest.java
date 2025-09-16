// Package
package persistence;

// Imports
import entities.Member;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class MemberMapperTest {

    // Attributes
    private static Database db;
    private static MemberMapper memberMapper;

    // ____________________________________________________________________

    @BeforeAll
    public static void setUpClass() {
        try {
            DatabaseConfig config = new DatabaseConfig();
            db = new Database(config.getUsername(), config.getPassword(), config.getUrl());
            memberMapper = new MemberMapper(db);

            // Setup test schema
            try (Connection testConnection = db.getConnection();
                 Statement stmt = testConnection.createStatement()) {

                stmt.execute("DROP TABLE IF EXISTS test.registration");
                stmt.execute("DROP TABLE IF EXISTS test.team");
                stmt.execute("DROP TABLE IF EXISTS test.sport");
                stmt.execute("DROP TABLE IF EXISTS test.member");
                stmt.execute("DROP TABLE IF EXISTS test.zip");

                stmt.execute("DROP SEQUENCE IF EXISTS test.member_member_id_seq CASCADE;");
                stmt.execute("DROP SEQUENCE IF EXISTS test.sport_sport_id_seq CASCADE;");

                stmt.execute("CREATE TABLE test.zip AS (SELECT * from public.zip) WITH NO DATA");
                stmt.execute("CREATE TABLE test.sport AS (SELECT * from public.sport) WITH NO DATA");
                stmt.execute("CREATE TABLE test.team AS (SELECT * from public.team) WITH NO DATA");
                stmt.execute("CREATE TABLE test.member AS (SELECT * from public.member) WITH NO DATA");
                stmt.execute("CREATE TABLE test.registration AS (SELECT * from public.registration) WITH NO DATA");

                stmt.execute("CREATE SEQUENCE test.member_member_id_seq");
                stmt.execute("ALTER TABLE test.member ALTER COLUMN member_id SET DEFAULT nextval('test.member_member_id_seq')");
                stmt.execute("CREATE SEQUENCE test.sport_sport_id_seq");
                stmt.execute("ALTER TABLE test.sport ALTER COLUMN sport_id SET DEFAULT nextval('test.sport_sport_id_seq')");

            } catch (SQLException e) {
                e.printStackTrace();
                fail("Database setup failed");
            }

        } catch (Exception e) {
            e.printStackTrace();
            fail("DatabaseConfig or Database initialization failed");
        }
    }

    // ____________________________________________________________________

    @BeforeEach
    void setUp() throws DatabaseException {
        try (Connection testConnection = db.getConnection();
             Statement stmt = testConnection.createStatement()) {

            stmt.execute("DELETE FROM test.registration");
            stmt.execute("DELETE FROM test.team");
            stmt.execute("DELETE FROM test.sport");
            stmt.execute("DELETE FROM test.member");
            stmt.execute("DELETE FROM test.zip");

            stmt.execute("SELECT setval('test.member_member_id_seq', 1)");

            stmt.execute("INSERT INTO test.zip VALUES " +
                    "(3700, 'Rønne'), (3730, 'Nexø'), (3740, 'Svanneke'), " +
                    "(3760, 'Gudhjem'), (3770, 'Allinge'), (3782, 'Klemmensker')");

            stmt.execute("INSERT INTO test.member (member_id, name, address, zip, gender, year) VALUES " +
                    "(1, 'Hans Sørensen', 'Agernvej 3', 3700, 'm', 2000), " +
                    "(2, 'Jens Kofoed', 'Agrevej 5', 3700, 'm', 2001), " +
                    "(3, 'Peter Hansen', 'Ahlegårdsvejen 7', 3700, 'm', 2002)");

            stmt.execute("SELECT setval('test.member_member_id_seq', COALESCE((SELECT MAX(member_id)+1 FROM test.member), 1), false)");

        } catch (SQLException e) {
            fail("Database setup failed");
        }
    }

    // ____________________________________________________________________

    @Test
    void testConnection() throws DatabaseException {
        assertNotNull(db.getConnection());
    }

    // ____________________________________________________________________

    @Test
    void getAllMembers() throws DatabaseException {
        List<Member> members = memberMapper.getAllMembers();
        assertEquals(3, members.size());
        assertEquals(members.get(0), new Member(1,"Hans Sørensen", "Agernvej 3",3700, "Rønne","m",2000));
        assertEquals(members.get(1), new Member(2, "Jens Kofoed","Agrevej 5",3700,"Rønne","m",2001));
        assertEquals(members.get(2), new Member(3, "Peter Hansen","Ahlegårdsvejen 7",3700,"Rønne","m",2002));
    }

    // ____________________________________________________________________

    @Test
    void getMemberById() throws DatabaseException {
        assertEquals(new Member(3, "Peter Hansen","Ahlegårdsvejen 7",3700,"Rønne","m",2002), memberMapper.getMemberById(3));
    }

    // ____________________________________________________________________

    @Test
    void deleteMember() throws DatabaseException {
        assertTrue(memberMapper.deleteMember(2));
        assertEquals(2, memberMapper.getAllMembers().size());
    }

    // ____________________________________________________________________

    @Test
    void insertMember() throws DatabaseException, IllegalInputException {
        Member m1 = memberMapper.insertMember(new Member("Jon Snow","Wintherfell 3", 3760, "Gudhjem", "m", 1992));
        assertNotNull(m1);
        assertEquals(4, memberMapper.getAllMembers().size());
        assertEquals(m1, memberMapper.getMemberById(4));
    }

    // ____________________________________________________________________

    @Test
    void updateMember() throws DatabaseException {
        boolean result = memberMapper.updateMember(new Member(2, "Jens Kofoed","Agrevej 5",3760,"Gudhjem","m",1999));
        assertTrue(result);
        Member m1 = memberMapper.getMemberById(2);
        assertEquals(1999,m1.getYear());
        assertEquals(3, memberMapper.getAllMembers().size());
    }

} // MemberMapperTest End