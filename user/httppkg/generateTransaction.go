package main

import (
	"encoding/json"
	"net/http"
	_ "net/http"

	"github.com/gorilla/mux"
)

// Request 구조체
type Request struct {
	Address string
	T       *Transaction
}

// 넘어온 Transaction 을 담기 위한 구조체
type Transaction struct {
	Txid      [32]byte // 거래 ID
	TimeStamp []byte   // 거래시간
	Applier   []byte   // 요청자
	Company   []byte   // 경력회사
	Career    []byte   // 경력기간
	Payment   []byte   // 결제수단
	Job       []byte   // 직종 , 업무
	Proof     []byte   // pdf 링크
}

// Transaction을 담기 위한 배열
type Transactions struct {
	Txs map[[32]byte]*Transaction
}

//Json 타입으로 리턴해주기 위한 구조체
type JsonResponse struct {
	Address string   `json:"address"`
	Txid    [32]byte `json:"txid"`
}

// 비어있는 Txs를 만드는 Method
func NewTransactions() *Transactions {
	Txs := &Transactions{}
	Txs.Txs = make(map[[32]byte]*Transaction)
	return Txs
}
func (Txs *Transactions) PutTransaction(tx *Transaction) [32]byte {
	Txs.Txs[tx.Txid] = tx
	return tx.Txid
}
func main() {
	// Txid := ApplyCareer(request)
	// fmt.Println(Txid, "Txid 입니다 ")
	request := &Request{}
	router := mux.NewRouter()
	router.HandleFunc("/Apply/Career", request.ApplyCareer).Methods("Post")
}

// Generate Transaction
func (r *Request) ApplyCareer(w http.ResponseWriter, req *http.Request) {
	Txs := NewTransactions()        // 비어있는 TXS를 만들고
	Txid := Txs.PutTransaction(r.T) // 그 비어있는 TXS안에 Transaction을 넣고 Txid를 반환
	var response = JsonResponse{Address: r.Address, Txid: Txid}
	json.NewEncoder(w).Encode(response)
}
