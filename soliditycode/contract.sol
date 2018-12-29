pragma solidity ^0.5.2;
contract Gunfactory{
	
	struct Gun {
	    string name; 
	    uint damage;
	    uint price;
	    string abrasion;
	}

	struct User {
		string username;
		string password;
		uint wallet;
		uint gunid;
	}
	uint randNonce = 0;
	uint Gun_price = 100;
	
	User[] public UserList;
	Gun[] public guns;

	function Gunfactory() public {
		guns.push(Gun("pistol",25, 45, "new"));
		guns.push(Gun("SMG",35, 75, "minimal wear"));
		guns.push(Gun("AR",45, 150, "battle-tested"));
	}

	function CreateUser(string name, string psd)  public returns (bool) {
		UserList.push(User({
		    username: name,
		    password: psd,
		    wallet: 100,
		    gunid: 4
		}));
		return true;
	}
	
	function Login(string username, string password) view public returns (bool) {
		for(uint i = 0; i < UserList.length; i++) {
			if (keccak256(username) == keccak256(UserList[i].username) 
				&& keccak256(password) == keccak256(UserList[i].password))
				return true;
		}
		return false;
	}

	function CreateGun(uint id) private returns (string) {
		if (UserList[id].wallet >= 100 ) {
			UserList[id].wallet -= 100;
			uint random = uint(keccak256(now, msg.sender, randNonce)) % 3;
			randNonce++;
			UserList[id].gunid = random;
			return "You get a gun COST:100$!";
		}
		else{
			return "You dont have enough $";
		}
	}

	function Register(string username, string password)  public returns (string) {
        for(uint i = 0; i < UserList.length; i++) {
          if (keccak256(username) == keccak256(UserList[i].username))
          	return "false has been Registered";
        }
        CreateUser(username,password);
        return "true Registered succeed";
	}


	function Opencase(string username, string password) public returns (string){
		if (Login(username,password)) {
			for(uint i = 0; i < UserList.length; i++) {
				if (keccak256(username) == keccak256(UserList[i].username)) {
					return CreateGun(i);
				}
			}
		}
	}

	function Getmygun(string username, string password) view public returns (string){
		if (Login(username, password)) {
			for(uint i = 0; i < UserList.length; i++) {
				if (keccak256(username) == keccak256(UserList[i].username)) {
					if (UserList[i].gunid == 4)
						return "You dont have any guns";
					else return guns[UserList[i].gunid].name;	
				}
			}
		}
		else {
			return "Please enter the right username or password";
		}
	}

}