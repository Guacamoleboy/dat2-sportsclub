// Imports
import entities.Member;
import persistence.Database;
import persistence.MemberMapper;
import persistence.DatabaseConfig;
import persistence.DatabaseException;
import java.util.List;

public class Main {

    // Attributes

    // Moved to "resources/config.properties" for safety reasons
    // Handeled by "persistence/DatabaseConfig.java"
    // Executed by "persistence/Database.java"

    // - Guac

    // _______________________________________________________

    public static void main(String[] args) {

        try {

            DatabaseConfig config = new DatabaseConfig();
            Database db = new Database(config.getUsername(), config.getPassword(), config.getUrl());
            MemberMapper memberMapper = new MemberMapper(db);

            // Hent og vis alle medlemmer
            List<Member> members = memberMapper.getAllMembers();
            showMembers(members);

            // Vis medlem med ID 13
            showMemberById(memberMapper, 13);

            // Tasks
            showList("Participants per team", memberMapper.membersPerTeam());
            showList("Participants per sport", memberMapper.membersPerSport());
            showList("Men & Women", memberMapper.membersByGender());
            showList("Total income", memberMapper.totalIncomeAllTeams());
            showList("Income per team", memberMapper.totalIncomePerTeam());
            showList("Pay per team", memberMapper.averagePaymentPerTeam());

        } catch (Exception e) {
            System.out.println("Database error: " + e.getMessage());
            if (e.getCause() != null) {
                System.out.println("Cause: " + e.getCause().getMessage());
            }
            e.printStackTrace();
        }

    }

    // _______________________________________________________

    private static void deleteMember(int memberId, MemberMapper memberMapper) {
        try {
            if (memberMapper.deleteMember(memberId)) {
                System.out.println("Member with id = " + memberId + " is removed from DB");
            }
        } catch (DatabaseException e) {
            System.out.println("Fejl ved sletning af medlem: " + e.getMessage());
        }
    }

    // _______________________________________________________

    private static int insertMember(MemberMapper memberMapper) {
        try {
            Member m1 = new Member("Ole Olsen", "Banegade 2", 3700, "Rønne", "m", 1967);
            Member m2 = memberMapper.insertMember(m1);
            showMemberById(memberMapper, m2.getMemberId());
            return m2.getMemberId();
        } catch (DatabaseException e) {
            System.out.println("Fejl ved indsættelse af medlem: " + e.getMessage());
            return -1;
        }
    }

    // _______________________________________________________

    private static void updateMember(int memberId, MemberMapper memberMapper) {
        try {
            Member m1 = memberMapper.getMemberById(memberId);
            m1.setYear(1970);
            if (memberMapper.updateMember(m1)) {
                showMemberById(memberMapper, memberId);
            }
        } catch (DatabaseException e) {
            System.out.println("Fejl ved opdatering af medlem: " + e.getMessage());
        }
    }

    // _______________________________________________________

    private static void showMemberById(MemberMapper memberMapper, int memberId) {
        try {
            System.out.println("***** Vis medlem nr. " + memberId + ": *******");
            System.out.println(memberMapper.getMemberById(memberId));
        } catch (DatabaseException e) {
            System.out.println("Fejl ved hentning af medlem: " + e.getMessage());
        }
    }

    // _______________________________________________________

    private static void showMembers(List<Member> members) {
        System.out.println("***** Vis alle medlemmer *******");
        for (Member member : members) {
            System.out.println(member);
        }
    }

    // _______________________________________________________

    private static void showList(String title, List<String> input) {
        System.out.println("***** " + title + " *****");
        for (String i : input) {
            System.out.println(i);
        }
    }

} // Main End