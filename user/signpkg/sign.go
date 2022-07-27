package signpkg

import (
	"encoding/hex"
	"encoding/json"
	"fmt"
	"net/http"

	"github.com/heoseoul/cabb/user/blockpkg"
	"github.com/heoseoul/cabb/user/txpkg"
	"github.com/heoseoul/cabb/user/walletpkg"
)

type Testt struct {
	Applier string `json:"Applier"`
	Company string `json:"Company"`
	Career  string `json:"Career"`
	Payment string `json:"Payment"`
	Job     string `json:"Job"`
	Proof   string `json:"Proof"`
	WAddr   string `json:"WAddr"`
}

func AddCareer(res http.ResponseWriter, req *http.Request) {
	fmt.Println()
	fmt.Println("method : ", req.Method)
	fmt.Println("url : ", req.URL)
	fmt.Println("Header : ", req.Header)

	defer req.Body.Close()

	fmt.Println("req.Body : ", req.Body)
	a := json.NewDecoder(req.Body)
	// a.DisallowUnknownFields()
	var ttt Testt
	er := a.Decode(&ttt)
	if er != nil {
		fmt.Println(er)
	}
	fmt.Printf("json -> golang :  %+v\n", ttt)

	wal := walletpkg.NewWallet("hi")
	tx1 := txpkg.NewTx(
		wal.Address,
		ttt.Company,
		ttt.Career,
		ttt.Payment,
		ttt.Job,
		ttt.Proof,
		ttt.WAddr,
	)

	tx1.PrintTx()

	tx1.Sign = txpkg.TxSign(tx1.TxID, &wal.PrvKey)
	fmt.Println("Sign : ", hex.EncodeToString(tx1.Sign))

	crea := txpkg.CreateTxDB()
	crea.AddTx(tx1)
	bl := &blockpkg.Block{}
	if crea.CheckSign(tx1.TxID, &wal.PrvKey.PublicKey) {
		bl = blockpkg.NewBlock([32]byte{}, 1, tx1.TxID)
		fmt.Print("검증 성공, 블록 생성 완료\n\n블록 해시 : ")
	} else {
		fmt.Println("검증 실패")
	}

	for _, j := range bl.Hash {
		fmt.Print(hex.EncodeToString([]byte(string(j))))
	}

}
