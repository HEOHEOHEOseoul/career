package httppkg

import (
	"bytes"
	"cabb/user/txpkg"
	"encoding/hex"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"net/http"
	_ "net/http"
)

// Request 구조체
type Request struct {
	Address string `json:"address"`
	Data    string `json:"data"`
	//T       *txpkg.Tx `json:"transaction"`
	Applier string `json:"applier"`
	Company string `json:"company"`
	Career  string `json:"career"`
	Payment string `json:"payment"`
	Job     string `json:"job"`
	Proof   string `json:"proof"`
}

//Json 타입으로 리턴해주기 위한 구조체
type JsonResponse struct {
	Address string   `json:"address"`
	Txid    [32]byte `json:"txid"`
}

// Generate Transaction
func ApplyCareer(w http.ResponseWriter, req *http.Request) {
	var body Request

	decoder := json.NewDecoder(req.Body)
	decoder.DisallowUnknownFields()
	err := decoder.Decode(&body)
	//에러 체크
	if err != nil {
		fmt.Print(err)
		return
	}

	T := txpkg.NewTx(body.Applier, body.Company, body.Career, body.Payment, body.Job, body.Proof, body.Address)

	Txs := txpkg.CreateTxDB() // [임시] 최초에 만들어서 운용중인 Txs(DB) 가져와야함
	Txid := Txs.AddTx(T)      // Txs(임시)에 트랜잭션 등록
	T.PrintTx()
	fmt.Println("Tx-TxID: ", T.TxID)
	value := map[string]string{
		"txID": hex.EncodeToString(T.TxID[:]),
		"data": body.Data}
	json_data, _ := json.Marshal(value)
	resp, err := http.Post("http://localhost:9000/newBlk", "application/json", bytes.NewBuffer(json_data))
	if err != nil {
		panic(err)
	}
	defer resp.Body.Close()

	// Response 체크.
	respBody, err := ioutil.ReadAll(resp.Body)
	if err == nil {
		str := string(respBody)
		println(str)
	}

	var response = JsonResponse{Address: body.Address, Txid: Txid}
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(response)
}
