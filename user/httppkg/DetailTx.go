package httppkg

import (
	"cabb/user/blockpkg"
	"cabb/user/txpkg"
	"encoding/json"
	"fmt"
	"net/http"
)

// Request 구조체
type DetailTxRequest struct {
	TxId string `json:"txID"`
}

type JsonDetailResponse struct {
	Hash      [32]byte `json:"blockID"`
	Data      []byte   `json:"Data"`
	Timestamp []byte   `json:"Timestamp"`
	Txid      [32]byte `json:"Txid"`
	Applier   []byte   `json:"Applier"`
	Company   []byte   `json:"Company"`
	Career    []byte   `json:"Career"`
	Job       []byte   `json:"Job"`
	Proof     []byte   `json:"Proof"`
}

//Json 타입으로 리턴해주기 위한 구조체

// func main() {
// 	request := &Request{}
// 	router := mux.NewRouter()
// 	router.HandleFunc("/detailTx", request.ApplyCareer).Methods("Post")

// 	log.Fatal(http.ListenAndServe(":8080", router))
// }

func GChain() (*txpkg.Txs, *blockpkg.Blocks) {
	txs := txpkg.CreateTxDB()
	fmt.Println("txs", txs)
	g := blockpkg.GenesisBlock()
	g.PrintBlock()
	bs := blockpkg.NewBlockchain(g)
	prevH := g.Hash
	fmt.Println("prevH", prevH)

	for i := 0; i < 10; i++ {
		tx := txpkg.NewTx("a", "b", "c", "d", "e", "f", "g")
		txs.AddTx(tx)
		tx.PrintTx()
		b := blockpkg.NewBlock(prevH, len(bs.BlockChain), tx.TxID, "data")
		b.PrintBlock()
		prevH = b.Hash
		bs.AddBlock(b)
	}
	return txs, bs
}

func DetailTx(w http.ResponseWriter, req *http.Request) {
	var body DetailTxRequest

	// txs := txpkg.CreateTxDB()
	// fmt.Println("txs", txs)
	// g := blockpkg.GenesisBlock()
	// g.PrintBlock()
	// bs := blockpkg.NewBlockchain(g)
	// prevH := g.Hash
	// fmt.Println("prevH", prevH)

	// for i := 0; i < 10; i++ {
	// 	tx := txpkg.NewTx("a", "b", "c", "d", "e", "f", "g")
	// 	txs.AddTx(tx)
	// 	tx.PrintTx()
	// 	b := blockpkg.NewBlock(prevH, len(bs.BlockChain), tx.TxID)
	// 	b.PrintBlock()
	// 	prevH = b.Hash
	// 	bs.AddBlock(b)
	// }

	decoder := json.NewDecoder(req.Body)
	decoder.DisallowUnknownFields()
	err := decoder.Decode(&body)
	if err != nil {
		fmt.Print(err)
		return
	}

	tmp := []byte(body.TxId)
	var txID [32]byte
	copy(txID[:], tmp)
	//fmt.Println("txID: ", txID)

	f := txpkg.FindBlockByTx(txID, bs)
	t := txpkg.FindTxByTxid(txID, txs)
	if f != nil {
		var response = JsonDetailResponse{Hash: f.Hash, Data: f.Data,
			Timestamp: f.Timestamp, Txid: f.Txid, Applier: t.Applier,
			Company: t.Company, Career: t.Career, Job: t.Job, Proof: t.Proof}
		json.NewEncoder(w).Encode(response)
	} else {
		fmt.Println("Txid 가 없습니다.")
	}
}
