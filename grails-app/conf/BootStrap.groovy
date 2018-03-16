import com.pathunt.User
import com.pathunt.Role
import com.pathunt.UserRole

class BootStrap {

    def init = { servletContext ->
        def userCount = User.findAll().size()
        if(!userCount) {
            User admin = new User(username: 'admin', password: 'iam@Admin', enabled: true).save(failOnError: true)
            User user1 = new User(username: 'user1', password: 'iam@UserA', enabled: true).save(failOnError: true)
            User user2 = new User(username: 'user2', password: 'iam@UserB', enabled: true).save(failOnError: true)
            User user3 = new User(username: 'user3', password: 'iam@UserC', enabled: true).save(failOnError: true)
            User user4 = new User(username: 'user4', password: 'iam@UserD', enabled: true).save(failOnError: true)
            User user5 = new User(username: 'user5', password: 'iam@UserE', enabled: true).save(failOnError: true)
            User user6 = new User(username: 'user6', password: 'iam@UserF', enabled: true).save(failOnError: true)
            User user7 = new User(username: 'user7', password: 'iam@UserG', enabled: true).save(failOnError: true)
            User user8 = new User(username: 'user8', password: 'iam@UserH', enabled: true).save(failOnError: true)
            User user9 = new User(username: 'user9', password: 'iam@UserI', enabled: true).save(failOnError: true)
            User user10 = new User(username: 'user10', password: 'iam@UserJ', enabled: true).save(failOnError: true)
            User user11 = new User(username: 'user11', password: 'iam@UserK', enabled: true).save(failOnError: true)
            User user12 = new User(username: 'user12', password: 'iam@UserL', enabled: true).save(failOnError: true)
            User user13 = new User(username: 'user13', password: 'iam@UserM', enabled: true).save(failOnError: true)
            User user14 = new User(username: 'user14', password: 'iam@UserN', enabled: true).save(failOnError: true)
            User user15 = new User(username: 'user15', password: 'iam@UserO', enabled: true).save(failOnError: true)
            def roleCount = Role.findAll().size()
            if (!roleCount) {
                Role role_admin = new Role(authority: 'ROLE_ADMIN').save()
                def userRole = UserRole.findAll().size()
                if (!userRole) {
                    UserRole.create(admin, role_admin)
                    UserRole.create(user1, role_admin)
                    UserRole.create(user2, role_admin)
                    UserRole.create(user3, role_admin)
                    UserRole.create(user4, role_admin)
                    UserRole.create(user5, role_admin)
                    UserRole.create(user6, role_admin)
                    UserRole.create(user7, role_admin)
                    UserRole.create(user8, role_admin)
                    UserRole.create(user9, role_admin)
                    UserRole.create(user10, role_admin)
                    UserRole.create(user11, role_admin)
                    UserRole.create(user12, role_admin)
                    UserRole.create(user13, role_admin)
                    UserRole.create(user14, role_admin)
                    UserRole.create(user15, role_admin)
                }
            }
        }
        def asciiArt = """
PPPPPPPPPPPPPPPPP        AAA         TTTTTTTTTTTTTTTTTTTTTTTHHHHHHHHH     HHHHHHHHHUUUUUUUU     UUUUUUUUNNNNNNNN        NNNNNNNNTTTTTTTTTTTTTTTTTTTTTTT
P::::::::::::::::P      A:::A        T:::::::::::::::::::::TH:::::::H     H:::::::HU::::::U     U::::::UN:::::::N       N::::::NT:::::::::::::::::::::T
P::::::PPPPPP:::::P    A:::::A       T:::::::::::::::::::::TH:::::::H     H:::::::HU::::::U     U::::::UN::::::::N      N::::::NT:::::::::::::::::::::T
PP:::::P     P:::::P  A:::::::A      T:::::TT:::::::TT:::::THH::::::H     H::::::HHUU:::::U     U:::::UUN:::::::::N     N::::::NT:::::TT:::::::TT:::::T
  P::::P     P:::::P A:::::::::A     TTTTTT  T:::::T  TTTTTT  H:::::H     H:::::H   U:::::U     U:::::U N::::::::::N    N::::::NTTTTTT  T:::::T  TTTTTT
  P::::P     P:::::PA:::::A:::::A            T:::::T          H:::::H     H:::::H   U:::::D     D:::::U N:::::::::::N   N::::::N        T:::::T        
  P::::PPPPPP:::::PA:::::A A:::::A           T:::::T          H::::::HHHHH::::::H   U:::::D     D:::::U N:::::::N::::N  N::::::N        T:::::T        
  P:::::::::::::PPA:::::A   A:::::A          T:::::T          H:::::::::::::::::H   U:::::D     D:::::U N::::::N N::::N N::::::N        T:::::T        
  P::::PPPPPPPPP A:::::A     A:::::A         T:::::T          H:::::::::::::::::H   U:::::D     D:::::U N::::::N  N::::N:::::::N        T:::::T        
  P::::P        A:::::AAAAAAAAA:::::A        T:::::T          H::::::HHHHH::::::H   U:::::D     D:::::U N::::::N   N:::::::::::N        T:::::T        
  P::::P       A:::::::::::::::::::::A       T:::::T          H:::::H     H:::::H   U:::::D     D:::::U N::::::N    N::::::::::N        T:::::T        
  P::::P      A:::::AAAAAAAAAAAAA:::::A      T:::::T          H:::::H     H:::::H   U::::::U   U::::::U N::::::N     N:::::::::N        T:::::T        
PP::::::PP   A:::::A             A:::::A   TT:::::::TT      HH::::::H     H::::::HH U:::::::UUU:::::::U N::::::N      N::::::::N      TT:::::::TT      
P::::::::P  A:::::A               A:::::A  T:::::::::T      H:::::::H     H:::::::H  UU:::::::::::::UU  N::::::N       N:::::::N      T:::::::::T      
P::::::::P A:::::A                 A:::::A T:::::::::T      H:::::::H     H:::::::H    UU:::::::::UU    N::::::N        N::::::N      T:::::::::T      
PPPPPPPPPPAAAAAAA                   AAAAAAATTTTTTTTTTT      HHHHHHHHH     HHHHHHHHH      UUUUUUUUU      NNNNNNNN         NNNNNNN      TTTTTTTTTTT      
"""
        println asciiArt
    }
    def destroy = {
    }
}
